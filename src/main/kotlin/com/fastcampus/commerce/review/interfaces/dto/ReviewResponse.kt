package com.fastcampus.commerce.review.interfaces.dto

import java.time.LocalDateTime

data class ReviewResponse(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val adminReply: AdminReply? = null,
    val productId: Long,
    val productName: String,
    val productThumbnail: String,
    val createdAt: LocalDateTime,
)

data class AdminReply(
    val content: String,
    val createdAt: LocalDateTime,
)
