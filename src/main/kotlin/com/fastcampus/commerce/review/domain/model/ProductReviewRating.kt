package com.fastcampus.commerce.review.domain.model

import com.querydsl.core.annotations.QueryProjection

data class ProductReviewRating
    @QueryProjection
    constructor(
        val average: Double,
        val totalCount: Long,
        val oneStarCount: Int,
        val twoStarsCount: Int,
        val threeStarsCount: Int,
        val fourStarsCount: Int,
        val fiveStarsCount: Int,
    ) {
        companion object {
            fun empty(): ProductReviewRating {
                return ProductReviewRating(
                    average = 0.0,
                    totalCount = 0L,
                    oneStarCount = 0,
                    twoStarsCount = 0,
                    threeStarsCount = 0,
                    fourStarsCount = 0,
                    fiveStarsCount = 0,
                )
            }
        }
    }
