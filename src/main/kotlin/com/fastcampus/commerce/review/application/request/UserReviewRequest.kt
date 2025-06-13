package com.fastcampus.commerce.review.application.request

import com.fastcampus.commerce.review.domain.model.SearchUserReviewCondition
import java.time.LocalDateTime

data class UserReviewRequest(
    val userId: Long,
    val period: Int? = null,
) {
    fun toCondition(now: LocalDateTime): SearchUserReviewCondition {
        val to = if (period == null) null else now.toLocalDate()
        val from = if (period == null) null else to?.minusMonths(period.toLong())
        return SearchUserReviewCondition(
            userId = userId,
            to = to,
            from = from,
        )
    }
}
