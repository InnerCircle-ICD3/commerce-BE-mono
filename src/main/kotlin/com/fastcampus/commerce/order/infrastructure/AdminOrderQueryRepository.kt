package com.fastcampus.commerce.order.infrastructure

import com.fastcampus.commerce.admin.order.interfaces.request.AdminOrderSearchRequest
import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.entity.QOrder
import com.fastcampus.commerce.order.domain.entity.QOrderItem
import com.fastcampus.commerce.order.domain.entity.QProductSnapshot
import com.fastcampus.commerce.user.domain.entity.QUser
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class AdminOrderQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun searchOrders(search: AdminOrderSearchRequest, pageable: Pageable): Page<Order> {
        val whereCondition = BooleanBuilder()
            .and(orderStatusEq(search.status))
            .and(orderNumberEq(search.orderNumber))
            .and(userNicknameEq(search.nickname))
            //.and(orderedAtBetween(search.from, search.to))
            .and(
                JPAExpressions
                    .selectOne()
                    .from(QOrderItem.orderItem)
                    .join(QProductSnapshot.productSnapshot).on(
                        QOrderItem.orderItem.productSnapshotId.eq(
                            QProductSnapshot.productSnapshot.id,
                        ),
                    )
                    .where(
                        QOrderItem.orderItem.orderId.eq(QOrder.order.id),
                        (productNameContainsIgnoreCase(search.productName)),
                    )
                    .exists(),
            )

        val baseQuery = jpaQueryFactory
            .from(QOrder.order)
            .join(QUser.user).on(QOrder.order.userId.eq(QUser.user.id))
            .where(whereCondition)

        return PageableExecutionUtils.getPage(
            baseQuery
                .select(QOrder.order)
                .orderBy(QOrder.order.createdAt.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch(),
            pageable,
        ) {
            jpaQueryFactory
                .select(QOrder.order.id.count())
                .from(QOrder.order)
                .join(QUser.user).on(QOrder.order.userId.eq(QUser.user.id))
                .where(whereCondition)
                .fetchOne() ?: 0L
        }
    }

    private fun orderStatusEq(orderStatus: OrderStatus?) = if (orderStatus == null) null else QOrder.order.status.eq(orderStatus)

    private fun orderNumberEq(orderNumber: String?) = if (orderNumber == null) null else QOrder.order.orderNumber.eq(orderNumber)

    private fun userNicknameEq(nickname: String?) = if (nickname == null) null else QUser.user.nickname.eq(nickname)

    private fun productNameContainsIgnoreCase(productName: String?) =
        if (productName == null) {
            null
        } else {
            QProductSnapshot.productSnapshot.name.containsIgnoreCase(
                productName,
            )
        }

    private fun orderedAtBetween(from: LocalDate?, to: LocalDate?) =
        if (from == null || to == null) {
            null
        } else {
            val fromDateTime = from.atStartOfDay()
            val toDateTime = to.plusDays(1).atStartOfDay()

            QOrder.order.createdAt.goe(fromDateTime).and(QOrder.order.createdAt.lt(toDateTime))
        }
}
