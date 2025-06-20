package com.fastcampus.commerce.admin.order.interfaces

import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderSearchRequest
import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.entity.QOrder.order
import com.fastcampus.commerce.order.domain.entity.QOrderItem.orderItem
import com.fastcampus.commerce.order.domain.entity.QProductSnapshot.productSnapshot
import com.fastcampus.commerce.user.domain.entity.QUser.user
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
            .and(orderedAtBetween(search.from, search.to))
            .and(
                JPAExpressions
                    .selectOne()
                    .from(orderItem)
                    .join(productSnapshot).on(orderItem.productSnapshotId.eq(productSnapshot.id))
                    .where(
                        orderItem.orderId.eq(order.id),
                        (productNameContainsIgnoreCase(search.productName)),
                    )
                    .exists(),
            )

        val baseQuery = jpaQueryFactory
            .from(order)
            .join(user).on(order.userId.eq(user.id))
            .where(whereCondition)

        return PageableExecutionUtils.getPage(
            baseQuery
                .select(order)
                .orderBy(order.createdAt.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch(),
            pageable,
        ) {
            jpaQueryFactory
                .select(order.id.count())
                .from(order)
                .join(user).on(order.userId.eq(user.id))
                .where(whereCondition)
                .fetchOne() ?: 0L
        }
    }

    private fun orderStatusEq(orderStatus: OrderStatus?) = if (orderStatus == null) null else order.status.eq(orderStatus)

    private fun orderNumberEq(orderNumber: String?) = if (orderNumber == null) null else order.orderNumber.eq(orderNumber)

    private fun userNicknameEq(nickname: String?) = if (nickname == null) null else user.nickname.eq(nickname)

    private fun productNameContainsIgnoreCase(productName: String?) =
        if (productName == null) {
            null
        } else {
            productSnapshot.name.containsIgnoreCase(
                productName,
            )
        }

    private fun orderedAtBetween(from: LocalDate?, to: LocalDate?) =
        if (from == null || to == null) {
            null
        } else {
            val fromDateTime = from.atStartOfDay()
            val toDateTime = to.plusDays(1).atStartOfDay()

            order.createdAt.goe(fromDateTime).and(order.createdAt.lt(toDateTime))
        }
}
