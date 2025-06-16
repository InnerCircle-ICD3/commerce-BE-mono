package com.fastcampus.commerce.admin.order.infrastructure.request

data class AdminOrderUpdateRequest(
    val recipientName: String?,
    val recipientPhone: String?,
    val zipCode: String?,
    val address1: String?,
    val address2: String?,
    val deliveryMessage: String?,
    val adminMemo: String?
)
