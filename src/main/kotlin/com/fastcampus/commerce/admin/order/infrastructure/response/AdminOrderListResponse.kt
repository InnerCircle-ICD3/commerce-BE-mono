package com.fastcampus.commerce.admin.order.infrastructure.response

import java.time.LocalDateTime

data class AdminOrderListResponse(
    val orderId: Long,
    val orderNumber: String,
    val orderName: String,
    val orderStatus: String,
    val finalTotalPrice: Int,
    val orderedAt: LocalDateTime,
    val trackingNumber: String? = null,
    val customerId: Long,
    val customerName: String,
)
