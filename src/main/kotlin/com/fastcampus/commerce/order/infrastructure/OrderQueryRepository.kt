package com.fastcampus.commerce.order.infrastructure

import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.entity.QOrder.order
import com.fastcampus.commerce.order.domain.model.MyOrderCondition
import com.fastcampus.commerce.user.domain.entity.QUser.user
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class OrderQueryRepository(
    private val queryFactory: JPAQueryFactory,
) : QuerydslRepositorySupport(Order::class.java) {
    fun getUserOrders(condition: MyOrderCondition, pageable: Pageable): Page<Order> {
        val whereCondition = BooleanBuilder()
            .and(userIdEq(condition.userId))
            .and(orderStatusEq(condition.status))
            .and(createdAtBetween(condition.from, condition.to))

        val query = queryFactory
            .select(order)
            .from(order)
            .join(user).on(order.userId.eq(user.id))
            .where(whereCondition)
            .orderBy(order.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageableExecutionUtils.getPage(
            query,
            pageable,
        ) {
            queryFactory
                .select(order.id.count())
                .from(order)
                .join(user).on(order.userId.eq(user.id))
                .where(whereCondition)
                .fetchOne() ?: 0L
        }
    }

    private fun userIdEq(userId: Long) = user.id.eq(userId)

    private fun orderStatusEq(status: OrderStatus?) = if (status == null) null else order.status.eq(status)

    private fun createdAtBetween(from: LocalDate?, to: LocalDate?) =
        if (from == null || to == null) {
            null
        } else {
            val fromDateTime = from.atStartOfDay()
            val toDateTime = to.plusDays(1).atStartOfDay()

            order.createdAt.goe(fromDateTime).and(order.createdAt.lt(toDateTime))
        }
}
