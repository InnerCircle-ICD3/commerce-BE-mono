package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.review.domain.repository.ReviewRepository
import org.springframework.stereotype.Component

@Component
class ReviewReader(
    private val reviewRepository: ReviewRepository,
) {
    fun existsByUserIdAndOrderItemId(userId: Long, orderItemId: Long): Boolean {
        return reviewRepository.existsByUserIdAndOrderItemId(userId, orderItemId)
    }
}
