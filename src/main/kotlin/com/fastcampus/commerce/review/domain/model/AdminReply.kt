package com.fastcampus.commerce.review.domain.model

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class AdminReply
    @QueryProjection
    constructor(
        val content: String,
        val createdAt: LocalDateTime,
    )
