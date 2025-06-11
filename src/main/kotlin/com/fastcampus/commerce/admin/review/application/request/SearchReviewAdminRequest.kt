package com.fastcampus.commerce.admin.review.application.request

import com.fastcampus.commerce.review.domain.model.SearchReviewAdminCondition
import java.time.LocalDateTime

data class SearchReviewAdminRequest(
    val productId: Long? = null,
    val productName: String? = null,
    val rating: Int? = null,
    val content: String? = null,
    val period: Int? = null,
) {
    fun toCondition(now: LocalDateTime): SearchReviewAdminCondition {
        val to = if (period == null) null else now.toLocalDate()
        val from = if (period == null) null else to?.minusMonths(period.toLong())
        return SearchReviewAdminCondition(
            productId = productId,
            productName = productName,
            rating = rating,
            content = content,
            from = from,
            to = to,
        )
    }
}
