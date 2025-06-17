package com.fastcampus.commerce.admin.order.infrastructure.response

import java.time.LocalDateTime

data class AdminOrderDetailResponse(
    val orderNumber: String,
    val status: String,
    val createdAt: LocalDateTime,
    val paymentMethod: String,
    val address: String,
    val recipientName: String,
    val recipientPhone: String,
    val customerName: String,
    val customerEmail: String,
    val items: List<AdminOrderDetailItemResponse>,
    val subtotal: Int,
    val total: Int,
)

data class AdminOrderDetailItemResponse(
    val productName: String,
    val quantity: Int,
    val price: Int,
    val total: Int,
    val thumbnail: String?,
)
