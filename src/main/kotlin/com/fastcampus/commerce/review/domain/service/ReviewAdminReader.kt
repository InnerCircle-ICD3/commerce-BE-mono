package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.entity.ReviewReply
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.domain.model.ReviewInfo
import com.fastcampus.commerce.review.domain.model.SearchReviewAdminCondition
import com.fastcampus.commerce.review.domain.repository.ReviewAdminRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ReviewAdminReader(
    private val reviewAdminRepository: ReviewAdminRepository,
) {
    fun searchReviews(condition: SearchReviewAdminCondition, pageable: Pageable): Page<ReviewInfo> {
        return reviewAdminRepository.searchReviews(condition, pageable)
            .map(ReviewInfo::from)
    }

    fun getReview(reviewId: Long): ReviewInfo {
        return reviewAdminRepository.getReview(reviewId)?.let(ReviewInfo::from)
            ?: throw CoreException(ReviewErrorCode.REVIEW_NOT_FOUND)
    }

    fun getReply(replyId: Long): ReviewReply {
        return reviewAdminRepository.findReply(replyId)
            .orElseThrow { CoreException(ReviewErrorCode.REPLY_NOT_FOUND) }
    }
}
