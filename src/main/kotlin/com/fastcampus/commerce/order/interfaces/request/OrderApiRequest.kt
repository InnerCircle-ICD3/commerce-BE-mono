package com.fastcampus.commerce.order.interfaces.request

data class OrderApiRequest(
    val cartItemIds: Set<Long>,
    val shippingInfo: OrderShippingInfoApiRequest,
    val paymentMethod: String,
)

data class OrderShippingInfoApiRequest(
    val recipientName: String,
    val recipientPhone: String,
    val zipCode: String,
    val address1: String,
    val address2: String? = null,
    val deliveryMessage: String? = null,
)
