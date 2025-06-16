package com.fastcampus.commerce.admin.order.infrastructure.response

import java.time.LocalDateTime

data class AdminOrderCreateResponse(
    val orderId: Long,
    val orderNumber: String,
    val totalAmount: Int,
    val orderStatus: String,
    val orderedAt: LocalDateTime
)
