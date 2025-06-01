package com.fastcampus.commerce.review.interfaces.dto

data class ReviewRatingDistribution(
    val averageRating: Double,
    val ratingDistribution: RatingDistribution,
)

data class RatingDistribution(
    val oneStarCount: Int,
    val twoStarsCount: Int,
    val threeStarsCount: Int,
    val fourStarsCount: Int,
    val fiveStarsCount: Int,
)
