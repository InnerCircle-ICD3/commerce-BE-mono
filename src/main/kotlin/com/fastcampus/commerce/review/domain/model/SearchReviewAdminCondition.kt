package com.fastcampus.commerce.review.domain.model

import java.time.LocalDate

data class SearchReviewAdminCondition(
    val productId: Long? = null,
    val productName: String? = null,
    val rating: Int? = null,
    val content: String? = null,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
)
