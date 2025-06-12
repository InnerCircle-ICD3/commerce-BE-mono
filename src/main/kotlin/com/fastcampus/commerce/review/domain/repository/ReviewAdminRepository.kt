package com.fastcampus.commerce.review.domain.repository

import com.fastcampus.commerce.review.domain.entity.ReviewReply
import com.fastcampus.commerce.review.domain.model.ReviewAdminInfoFlat
import com.fastcampus.commerce.review.domain.model.SearchReviewAdminCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReviewAdminRepository {
    fun searchReviews(condition: SearchReviewAdminCondition, pageable: Pageable): Page<ReviewAdminInfoFlat>

    fun getReview(reviewId: Long): ReviewAdminInfoFlat?

    fun registerReply(reviewReply: ReviewReply): ReviewReply
}
