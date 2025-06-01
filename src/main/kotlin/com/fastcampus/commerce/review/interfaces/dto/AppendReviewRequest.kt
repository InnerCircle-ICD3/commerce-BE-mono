package com.fastcampus.commerce.review.interfaces.dto

data class AppendReviewRequest(
    val orderNumber: String,
    val orderItemId: Long,
    val rating: Int,
    val content: String,
)
