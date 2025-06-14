package com.fastcampus.commerce.order.interfaces.request

data class OrderApiRequest(
    val cartItemIds: Set<Long>,
    val shippingInfo: OrderShippingInfoApiRequest,
    val paymentMethod: String,
    val userId: Long,
)

data class OrderShippingInfoApiRequest(
    val recipientName: String,
    val recipientPhone: String,
    val zipCode: String,
    val address1: String,
    val address2: String? = null,
    val deliveryMessage: String? = null,
)
