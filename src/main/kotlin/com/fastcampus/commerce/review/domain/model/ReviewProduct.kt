package com.fastcampus.commerce.review.domain.model

import com.querydsl.core.annotations.QueryProjection

data class ReviewProduct
    @QueryProjection
    constructor(
        val productId: Long,
        val productName: String,
    )
