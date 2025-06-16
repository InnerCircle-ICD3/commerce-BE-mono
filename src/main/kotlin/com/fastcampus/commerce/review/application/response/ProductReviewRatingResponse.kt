package com.fastcampus.commerce.review.application.response

import com.fastcampus.commerce.review.domain.model.ProductReviewRating

data class ProductReviewRatingResponse(
    val averageRating: Double,
    val totalCount: Long,
    val ratingDistribution: RatingDistributionResponse,
) {
    companion object {
        fun from(response: ProductReviewRating) =
            ProductReviewRatingResponse(
                averageRating = response.average.toDouble(),
                totalCount = response.totalCount,
                ratingDistribution = RatingDistributionResponse(
                    oneStarCount = response.oneStarCount.toInt(),
                    twoStarsCount = response.twoStarsCount.toInt(),
                    threeStarsCount = response.threeStarsCount.toInt(),
                    fourStarsCount = response.fourStarsCount.toInt(),
                    fiveStarsCount = response.fiveStarsCount.toInt(),
                ),
            )
    }
}

data class RatingDistributionResponse(
    val oneStarCount: Int,
    val twoStarsCount: Int,
    val threeStarsCount: Int,
    val fourStarsCount: Int,
    val fiveStarsCount: Int,
)
