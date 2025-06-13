package com.fastcampus.commerce.review.domain.model

import java.time.LocalDate

data class SearchUserReviewCondition(
    val userId: Long,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
)
