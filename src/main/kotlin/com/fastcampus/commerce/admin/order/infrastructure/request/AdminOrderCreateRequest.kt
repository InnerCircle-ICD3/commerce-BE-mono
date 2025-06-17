package com.fastcampus.commerce.admin.order.infrastructure.request

import com.fastcampus.commerce.payment.domain.entity.PaymentStatus

data class AdminOrderCreateRequest(
    val userId: Long, // 주문자 회원ID
    val orderItems: List<OrderItemRequest>,
    val recipientName: String,
    val recipientPhone: String,
    val zipCode: String,
    val address1: String,
    val address2: String?,
    val deliveryMessage: String?,
    val paymentMethod: PaymentStatus,
)

data class OrderItemRequest(
    val productSnapshotId: Long,
    val quantity: Int,
)
