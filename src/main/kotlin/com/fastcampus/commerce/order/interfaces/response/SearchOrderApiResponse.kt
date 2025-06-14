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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    val orderedAt: LocalDateTime,
    val cancellable: Boolean,
    val refundable: Boolean,
)
