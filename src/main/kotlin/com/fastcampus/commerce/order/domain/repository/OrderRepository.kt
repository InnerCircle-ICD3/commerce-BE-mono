package com.fastcampus.commerce.order.domain.repository

import com.fastcampus.commerce.order.domain.entity.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Order>
    fun findByOrderNumber(orderNumber: String): Order?
}
