package com.fastcampus.commerce.review.domain.model

import com.querydsl.core.annotations.QueryProjection

data class ReviewAuthor
    @QueryProjection
    constructor(
        val userId: Long,
        val nickname: String,
    )
