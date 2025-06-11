package com.fastcampus.commerce.review.domain.model

import java.time.LocalDateTime

data class ReviewAdminInfo(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val adminReply: AdminReply? = null,
    val user: ReviewAuthor,
    val product: ReviewProduct,
    val createdAt: LocalDateTime,
)
