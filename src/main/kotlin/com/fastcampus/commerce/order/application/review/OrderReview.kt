package com.fastcampus.commerce.order.application.review

import java.time.LocalDateTime

data class OrderReview(
    val deliveredAt: LocalDateTime?,
    val productId: Long,
)
