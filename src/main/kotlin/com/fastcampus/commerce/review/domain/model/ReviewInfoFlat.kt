package com.fastcampus.commerce.review.domain.model

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class ReviewInfoFlat
    @QueryProjection
    constructor(
        val reviewId: Long,
        val rating: Int,
        val content: String,
        val adminReplyContent: String? = null,
        val adminReplyCreatedAt: LocalDateTime? = null,
        val userId: Long,
        val userNickname: String,
        val productId: Long,
        val productName: String,
        val productThumbnail: String,
        val createdAt: LocalDateTime,
    )
