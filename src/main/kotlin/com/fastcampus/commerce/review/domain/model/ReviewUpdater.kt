package com.fastcampus.commerce.review.domain.model

data class ReviewUpdater(
    val id: Long,
    val rating: Int,
    val content: String,
    val userId: Long,
)
