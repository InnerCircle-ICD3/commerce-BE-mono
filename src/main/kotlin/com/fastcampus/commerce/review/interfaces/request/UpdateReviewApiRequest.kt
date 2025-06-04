package com.fastcampus.commerce.review.interfaces.request

import com.fastcampus.commerce.review.application.request.UpdateReviewRequest

data class UpdateReviewApiRequest (
    val rating: Int,
    val content: String,
) {
    fun toServiceRequest(): UpdateReviewRequest = UpdateReviewRequest(
        rating = rating,
        content = content,
    )
}
