package com.fastcampus.commerce.review.interfaces.dto

import java.time.LocalDateTime

data class MyReviewResponse(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val createdAt: LocalDateTime,
    val adminReply: AdminReply? = null,
    val productId: Long,
    val productName: String,
    val productThumbnail: String,
)
