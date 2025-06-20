package com.fastcampus.commerce.order.interfaces.response

import java.time.LocalDateTime

data class GetOrderApiResponse(
    val orderNumber: String,
    val orderName: String,
    val orderStatus: String,
    val trackingNumber: String? = null,
    val paymentNumber: String,
    val paymentMethod: String,
    val itemsSubTotal: Int,
    val shippingFee: Int,
    val finalTotalPrice: Int,
    val items: List<GetOrderItemApiResponse>,
    val shippingInfo: GetOrderShippingInfoApiResponse,
    val orderedAt: LocalDateTime,
    val paidAt: LocalDateTime?,
    val cancellable: Boolean,
    val cancelRequested: Boolean,
    val cancelledAt: LocalDateTime?,
    val refundable: Boolean,
    val refundRequested: Boolean,
    val refundRequestedAt: LocalDateTime?,
    val refunded: Boolean,
    val refundedAt: LocalDateTime?,
    val reviewable: Boolean,
    val reviewWritten: Boolean,
)

data class GetOrderItemApiResponse(
    val orderItemId: Long,
    val productId: Long,
    val name: String,
    val thumbnail: String,
    val unitPrice: Int,
    val quantity: Int,
    val itemSubTotal: Int,
)

data class GetOrderShippingInfoApiResponse(
    val recipientName: String,
    val recipientPhone: String,
    val zipCode: String,
    val address1: String,
    val address2: String? = null,
    val deliveryMessage: String? = null,
)
