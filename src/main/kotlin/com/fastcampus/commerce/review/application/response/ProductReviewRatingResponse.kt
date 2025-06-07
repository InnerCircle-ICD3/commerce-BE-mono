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
                averageRating = response.average,
                totalCount = response.totalCount,
                ratingDistribution = RatingDistributionResponse(
                    oneStarCount = response.oneStarCount,
                    twoStarsCount = response.twoStarsCount,
                    threeStarsCount = response.threeStarsCount,
                    fourStarsCount = response.fourStarsCount,
                    fiveStarsCount = response.fiveStarsCount,
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
