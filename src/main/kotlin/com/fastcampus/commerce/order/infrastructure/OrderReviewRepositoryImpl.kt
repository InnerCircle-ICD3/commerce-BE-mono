package com.fastcampus.commerce.order.infrastructure

import com.fastcampus.commerce.order.application.review.OrderReview
import com.fastcampus.commerce.order.domain.entity.QOrder.order
import com.fastcampus.commerce.order.domain.entity.QOrderItem.orderItem
import com.fastcampus.commerce.order.domain.entity.QProductSnapshot.productSnapshot
import com.fastcampus.commerce.order.domain.repository.OrderReviewRepository
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class OrderReviewRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderReviewRepository {
    override fun findOrderReview(orderNumber: String, orderItemId: Long): OrderReview? {
        return queryFactory
            .select(
                Projections.constructor(
                    OrderReview::class.java,
                    order.deliveredAt,
                    productSnapshot.productId,
                ),
            )
            .from(order)
            .join(orderItem).on(order.id.eq(orderItem.orderId))
            .join(productSnapshot).on(orderItem.productSnapshotId.eq(productSnapshot.id))
            .where(
                order.orderNumber.eq(orderNumber),
                orderItem.id.eq(orderItemId),
            )
            .fetchOne()
    }
}
