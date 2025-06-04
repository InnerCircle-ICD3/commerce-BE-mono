package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.entity.Review
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.domain.repository.ReviewRepository
import org.springframework.stereotype.Component

@Component
class ReviewReader(
    private val reviewRepository: ReviewRepository,
) {
    fun existsByUserIdAndOrderItemId(userId: Long, orderItemId: Long): Boolean {
        return reviewRepository.existsByUserIdAndOrderItemId(userId, orderItemId)
    }

    fun getReviewById(reviewId: Long): Review {
        return reviewRepository.findById(reviewId)
            .orElseThrow { throw CoreException(ReviewErrorCode.REVIEW_NOT_FOUND) }
    }
}
