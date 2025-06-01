package com.fastcampus.commerce.admin.review.interfaces.dto

import java.time.LocalDateTime

data class SearchReviewResponse(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val createdAt: LocalDateTime,
    val adminReply: AdminReply? = null,
    val productId: Long,
    val productName: String,
    val productThumbnail: String,
)

data class AdminReply(
    val content: String,
    val createdAt: LocalDateTime,
)
