package com.fastcampus.commerce.admin.order.infrastructure.response

import java.time.LocalDateTime

data class AdminOrderListResponse (
    val orderId: Long,
    val orderNumber: String,
    val productSummary: String,
    val orderDate: LocalDateTime,
    val customerName: String,
    val totalAmount: Long,
    val paymentDate: LocalDateTime?,
    val status: String
)
