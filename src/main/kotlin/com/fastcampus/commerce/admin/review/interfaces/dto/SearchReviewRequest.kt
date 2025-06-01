package com.fastcampus.commerce.admin.review.interfaces.dto

import java.time.LocalDate

data class SearchReviewRequest(
    val productId: Long? = null,
    val productName: String? = null,
    val content: String? = null,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
)
