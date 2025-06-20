package com.fastcampus.commerce.admin.order.infrastructure.response

import java.time.LocalDateTime

data class AdminOrderDetailResponse(
    val orderId: Long,
    val orderNumber: String,
    val orderStatus: String,
    val trackingNumber: String? = null,
    val paymentNumber: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val itemsSubTotal: Int,
    val shippingFee: Int,
    val finalTotalPrice: Int,
    val items: List<AdminOrderDetailItemResponse>,
    val shippingInfo: AdminOrderDetailShippingInfoResponse,
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
)

data class AdminOrderDetailItemResponse(
    val orderItemId: Long,
    val productId: Long,
    val name: String,
    val thumbnail: String,
    val unitPrice: Int,
    val quantity: Int,
    val itemSubTotal: Int,
)

data class AdminOrderDetailShippingInfoResponse(
    val recipientName: String,
    val recipientPhone: String,
    val zipCode: String,
    val address1: String,
    val address2: String? = null,
    val deliveryMessage: String? = null,
)
