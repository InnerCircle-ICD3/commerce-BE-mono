package com.fastcampus.commerce.admin.review.interfaces.request

import com.fastcampus.commerce.admin.review.application.request.SearchReviewAdminRequest
import com.fastcampus.commerce.common.error.CommonErrorCode
import com.fastcampus.commerce.common.error.CoreException

data class SearchReviewAdminApiRequest(
    val productId: Long? = null,
    val productName: String? = null,
    val rating: Int? = null,
    val content: String? = null,
    val period: Int? = null,
) {
    companion object {
        private val ALLOWED_PERIODS = listOf(3, 6, 9, 12)
    }

    init {
        rating?.let {
            if (it !in 1..5) {
                throw CoreException(CommonErrorCode.FIELD_ERROR, "별점은 1~5점 사이로 선택해주세요.")
            }
        }
        period?.let {
            if (it !in ALLOWED_PERIODS) {
                throw CoreException(CommonErrorCode.FIELD_ERROR, "기간은 ${ALLOWED_PERIODS.joinToString(", ")} 중 하나여야 합니다.")
            }
        }
    }

    fun toServiceRequest() =
        SearchReviewAdminRequest(
            productId = productId,
            productName = productName,
            rating = rating,
            content = content,
            period = period,
        )
}
