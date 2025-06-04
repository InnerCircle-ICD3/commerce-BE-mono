package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.review.domain.entity.Review
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.domain.model.ReviewRegister
import com.fastcampus.commerce.review.domain.model.ReviewUpdater
import com.fastcampus.commerce.review.domain.repository.ReviewRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReviewStore(
    private val timeProvider: TimeProvider,
    private val reviewReader: ReviewReader,
    private val reviewRepository: ReviewRepository,
) {
    @Transactional(readOnly = false)
    fun register(register: ReviewRegister): Review {
        Review.validateReviewWrittenDate(timeProvider.now(), register.deliveredAt)
        val alreadyWritten = reviewReader.existsByUserIdAndOrderItemId(register.userId, register.orderItemId)
        if (alreadyWritten) {
            throw CoreException(ReviewErrorCode.ALREADY_WRITE)
        }
        return reviewRepository.save(register.toReview())
    }

    @Transactional(readOnly = false)
    fun update(command: ReviewUpdater) {
        val review = reviewReader.getReviewById(command.id)
        if (review.userId != command.userId) {
            throw CoreException(ReviewErrorCode.UNAUTHORIZED_REVIEW_UPDATE)
        }
        review.update(command)
    }

    @Transactional(readOnly = false)
    fun delete(userId: Long, reviewId: Long) {
        val review = reviewReader.getReviewById(reviewId)
        if (review.userId != userId) {
            throw CoreException(ReviewErrorCode.UNAUTHORIZED_REVIEW_DELETE)
        }
        reviewRepository.delete(review)
    }
}
