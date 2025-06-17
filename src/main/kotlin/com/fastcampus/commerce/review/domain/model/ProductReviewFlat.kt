package com.fastcampus.commerce.review.domain.model

import java.time.LocalDateTime

data class ProductReviewFlat(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val createdAt: LocalDateTime,
    val replyContent: String? = null,
    val replyCreatedAt: LocalDateTime? = null,
    val userId: String,
    val userNickname: String,
)
