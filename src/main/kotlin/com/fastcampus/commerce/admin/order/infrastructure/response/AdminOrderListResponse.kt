package com.fastcampus.commerce.admin.order.infrastructure.response

import java.time.LocalDateTime

data class AdminOrderListResponse (
    val orderId: Long,
    val orderNumber: String,
    val productName: String,
    val productQuantity: Int,
    val productUnitPrice: Int,
    val orderDate: LocalDateTime,
    val customerName: String,
    val totalAmount: Int,
    val paymentDate: LocalDateTime?,
    val status: String
)
