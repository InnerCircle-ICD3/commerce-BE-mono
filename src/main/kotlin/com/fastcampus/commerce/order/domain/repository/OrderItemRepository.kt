package com.fastcampus.commerce.order.domain.repository

import com.fastcampus.commerce.order.domain.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Long> {
}
