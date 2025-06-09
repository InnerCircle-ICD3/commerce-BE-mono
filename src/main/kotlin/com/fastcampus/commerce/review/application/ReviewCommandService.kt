package com.fastcampus.commerce.review.application

import com.fastcampus.commerce.order.application.review.OrderReviewService
import com.fastcampus.commerce.review.application.request.RegisterReviewRequest
import com.fastcampus.commerce.review.application.request.UpdateReviewRequest
import com.fastcampus.commerce.review.domain.service.ReviewStore
import org.springframework.stereotype.Service

@Service
class ReviewCommandService(
    private val orderReviewService: OrderReviewService,
    private val reviewStore: ReviewStore,
) {
    fun registerReview(userId: Long, request: RegisterReviewRequest): Long {
        val info = orderReviewService.getReviewInfo(request.orderNumber, request.orderItemId)
        val review = reviewStore.register(request.toCommand(userId, info))
        return review.id!!
    }

    fun updateReview(userId: Long, reviewId: Long, request: UpdateReviewRequest): Long {
        reviewStore.update(request.toCommand(userId, reviewId))
        return reviewId
    }

    fun deleteReview(userId: Long, reviewId: Long) {
        reviewStore.delete(userId, reviewId)
    }
}
