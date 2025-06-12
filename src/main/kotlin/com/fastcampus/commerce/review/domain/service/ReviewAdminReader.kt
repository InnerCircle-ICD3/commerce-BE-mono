package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.domain.model.ReviewAdminInfo
import com.fastcampus.commerce.review.domain.model.SearchReviewAdminCondition
import com.fastcampus.commerce.review.domain.repository.ReviewAdminRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ReviewAdminReader(
    private val reviewAdminRepository: ReviewAdminRepository,
) {
    fun searchReviews(condition: SearchReviewAdminCondition, pageable: Pageable): Page<ReviewAdminInfo> {
        return reviewAdminRepository.searchReviews(condition, pageable)
            .map(ReviewAdminInfo::from)
    }

    fun getReview(reviewId: Long): ReviewAdminInfo {
        return reviewAdminRepository.getReview(reviewId)?.let(ReviewAdminInfo::from)
            ?: throw CoreException(ReviewErrorCode.REVIEW_NOT_FOUND)
    }
}
