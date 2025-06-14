package com.fastcampus.commerce.order.infrastructure

import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.repository.OrderPaymentRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class OrderPaymentRepositoryImpl(
    private val orderJpaRepository: OrderJpaRepository,
) : OrderPaymentRepository {
    override fun findOrderByOrderNumber(orderNumber: String): Optional<Order> {
        return orderJpaRepository.findByOrderNumber(orderNumber)
    }

    override fun findOrderById(orderId: Long): Optional<Order> {
        return orderJpaRepository.findById(orderId)
    }
}
