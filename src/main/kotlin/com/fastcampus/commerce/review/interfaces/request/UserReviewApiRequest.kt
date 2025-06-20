package com.fastcampus.commerce.review.interfaces.request

import com.fastcampus.commerce.common.error.CommonErrorCode
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.application.request.UserReviewRequest

data class UserReviewApiRequest(
    val monthRange: Int? = null,
) {
    companion object {
        private val ALLOWED_PERIODS = listOf(3, 6, 9, 12)
    }

    init {
        monthRange?.let {
            if (it !in ALLOWED_PERIODS) {
                throw CoreException(CommonErrorCode.FIELD_ERROR, "기간은 ${ALLOWED_PERIODS.joinToString(", ")} 중 하나여야 합니다.")
            }
        }
    }

    fun toServiceRequest(userId: Long) = UserReviewRequest(userId, monthRange)
}
