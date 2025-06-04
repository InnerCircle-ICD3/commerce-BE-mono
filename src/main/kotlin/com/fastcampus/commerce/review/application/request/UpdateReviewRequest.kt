package com.fastcampus.commerce.review.application.request

import com.fastcampus.commerce.review.domain.model.ReviewUpdater

data class UpdateReviewRequest(
    val rating: Int,
    val content: String,
) {
    fun toCommand(userId: Long, reviewId: Long): ReviewUpdater =
        ReviewUpdater(
            id = reviewId,
            rating = rating,
            content = content,
            userId = userId,
        )
}
