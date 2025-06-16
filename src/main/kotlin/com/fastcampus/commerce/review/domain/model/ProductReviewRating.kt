package com.fastcampus.commerce.review.domain.model

data class ProductReviewRating(
    val average: Double,
    val totalCount: Long,
    val oneStarCount: Long,
    val twoStarsCount: Long,
    val threeStarsCount: Long,
    val fourStarsCount: Long,
    val fiveStarsCount: Long,
) {
    companion object {
        fun empty(): ProductReviewRating {
            return ProductReviewRating(
                average = 0.0,
                totalCount = 0L,
                oneStarCount = 0L,
                twoStarsCount = 0L,
                threeStarsCount = 0L,
                fourStarsCount = 0L,
                fiveStarsCount = 0L,
            )
        }
    }
}
