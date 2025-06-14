package com.fastcampus.commerce.order.domain.repository

import com.fastcampus.commerce.order.domain.entity.Order
import java.util.Optional

interface OrderPaymentRepository {
    fun findOrderByOrderNumber(orderNumber: String): Optional<Order>
}
