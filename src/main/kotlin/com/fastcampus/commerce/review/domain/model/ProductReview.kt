package com.fastcampus.commerce.review.domain.model

import java.time.LocalDateTime

data class ProductReview(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val createdAt: LocalDateTime,
    val adminReply: AdminReply? = null,
)
