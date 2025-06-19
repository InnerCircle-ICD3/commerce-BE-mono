package com.fastcampus.commerce.order.interfaces.response

import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class SearchOrderApiResponse(
    val orderNumber: String,
    val orderName: String,
    val mainProductThumbnail: String,
    val orderStatus: OrderStatus,
    val finalTotalPrice: Int,
    val orderedAt: LocalDateTime,
    val trackingNumber: String? = null,
    val cancellable: Boolean,
    val refundable: Boolean,
)
