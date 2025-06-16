package com.fastcampus.commerce.review.domain.model

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class ProductReviewFlat
    @QueryProjection
    constructor(
        val reviewId: Long,
        val rating: Int,
        val content: String,
        val createdAt: LocalDateTime,
        val replyContent: String? = null,
        val replyCreatedAt: LocalDateTime? = null,
    )
