package com.fastcampus.commerce.order.interfaces.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class GetOrderApiResponse(
    val orderNumber: String,
    val orderName: String,
    val orderStatus: String,
    val paymentNumber: String,
    val paymentMethod: String,
    val itemsSubTotal: Int,
    val shippingFee: Int,
    val finalTotalPrice: Int,
    val items: List<GetOrderItemApiResponse>,
    val shippingInfo: GetOrderShippingInfoApiResponse,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    val orderedAt: LocalDateTime,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    val paidAt: LocalDateTime?,
    val cancellable: Boolean,
    val cancelRequested: Boolean,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    val cancelledAt: LocalDateTime?,
    val refundable: Boolean,
    val refundRequested: Boolean,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    val refundRequestedAt: LocalDateTime?,
    val refunded: Boolean,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    val refundedAt: LocalDateTime?,
    val reviewable: Boolean,
    val reviewWritten: Boolean,
)

data class GetOrderItemApiResponse(
    val orderItemId: Long,
    val productSnapshotId: Long,
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
