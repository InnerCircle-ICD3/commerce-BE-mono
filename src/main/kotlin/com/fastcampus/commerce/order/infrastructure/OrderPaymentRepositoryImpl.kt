package com.fastcampus.commerce.order.infrastructure

import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.QOrder.order
import com.fastcampus.commerce.order.domain.entity.QOrderItem.orderItem
import com.fastcampus.commerce.order.domain.entity.QProductSnapshot.productSnapshot
import com.fastcampus.commerce.order.domain.model.OrderProduct
import com.fastcampus.commerce.order.domain.repository.OrderPaymentRepository
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class OrderPaymentRepositoryImpl(
    private val orderJpaRepository: OrderJpaRepository,
    private val queryFactory: JPAQueryFactory,
) : OrderPaymentRepository {
    override fun findOrderByOrderNumber(orderNumber: String): Optional<Order> {
        return orderJpaRepository.findByOrderNumber(orderNumber)
    }

    override fun findOrderById(orderId: Long): Optional<Order> {
        return orderJpaRepository.findById(orderId)
    }

    override fun getOrderProducts(orderId: Long): List<OrderProduct> {
        return queryFactory
            .select(
                Projections.constructor(
                    OrderProduct::class.java,
                    orderItem.id,
                    productSnapshot.productId,
                    orderItem.quantity,
                ),
            )
            .from(order)
            .join(orderItem).on(order.id.eq(orderItem.orderId))
            .join(productSnapshot).on(productSnapshot.id.eq(orderItem.productSnapshotId))
            .where(order.id.eq(orderId))
            .fetch()
    }
}
