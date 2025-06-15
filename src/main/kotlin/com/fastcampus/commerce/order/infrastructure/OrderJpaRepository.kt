package com.fastcampus.commerce.order.infrastructure

import com.fastcampus.commerce.order.domain.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface OrderJpaRepository : JpaRepository<Order, Long> {
    fun findByOrderNumber(orderNumber: String): Optional<Order>
}
