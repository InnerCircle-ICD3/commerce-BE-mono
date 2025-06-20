package com.fastcampus.commerce.admin.review.interfaces.request

import com.fastcampus.commerce.admin.review.application.request.SearchReviewAdminRequest

data class SearchReviewAdminApiRequest(
    val productId: Long? = null,
    val productName: String? = null,
    val rating: Int? = null,
    val content: String? = null,
    val period: Int? = null,
) {
    fun toServiceRequest() =
        SearchReviewAdminRequest(
            productId = productId,
            productName = productName,
            rating = rating,
            content = content,
            period = period,
        )
}
