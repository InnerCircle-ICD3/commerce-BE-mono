package com.fastcampus.commerce.review.interfaces.request

import com.fastcampus.commerce.review.application.request.UserReviewRequest

data class UserReviewApiRequest(
    val period: Int? = null,
) {
    fun toServiceRequest(userId: Long) = UserReviewRequest(userId, period)
}
