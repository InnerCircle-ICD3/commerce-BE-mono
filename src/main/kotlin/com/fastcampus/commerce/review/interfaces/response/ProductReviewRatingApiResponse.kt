package com.fastcampus.commerce.review.interfaces.response

import com.fastcampus.commerce.review.application.response.ProductReviewRatingResponse
import com.fastcampus.commerce.review.application.response.RatingDistributionResponse

data class ProductReviewRatingApiResponse(
    val averageRating: Double,
    val totalCount: Long,
    val ratingDistribution: RatingDistributionApiResponse,
) {
    companion object {
        fun from(response: ProductReviewRatingResponse) =
            ProductReviewRatingApiResponse(
                averageRating = response.averageRating,
                totalCount = response.totalCount,
                ratingDistribution = RatingDistributionApiResponse.from(response.ratingDistribution),
            )
    }
}

data class RatingDistributionApiResponse(
    val oneStarCount: Int,
    val twoStarsCount: Int,
    val threeStarsCount: Int,
    val fourStarsCount: Int,
    val fiveStarsCount: Int,
) {
    companion object {
        fun from(response: RatingDistributionResponse) =
            RatingDistributionApiResponse(
                oneStarCount = response.oneStarCount,
                twoStarsCount = response.twoStarsCount,
                threeStarsCount = response.threeStarsCount,
                fourStarsCount = response.fourStarsCount,
                fiveStarsCount = response.fiveStarsCount,
            )
    }
}
