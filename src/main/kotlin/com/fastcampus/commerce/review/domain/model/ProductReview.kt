package com.fastcampus.commerce.review.domain.model

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class ProductReview
    @QueryProjection
    constructor(
        val reviewId: Long,
        val rating: Int,
        val content: String,
        val createdAt: LocalDateTime,
        val adminReply: AdminReply? = null,
    )

data class AdminReply
    @QueryProjection
    constructor(
        val content: String,
        val createdAt: LocalDateTime,
    )
