package com.fastcampus.commerce.order.domain.repository

import com.fastcampus.commerce.order.domain.entity.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
}
