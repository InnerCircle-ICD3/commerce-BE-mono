package com.fastcampus.commerce.review.domain.repository

import com.fastcampus.commerce.review.domain.entity.Review
import java.util.Optional

interface ReviewRepository {
    fun existsByUserIdAndOrderItemId(userId: Long, orderItemId: Long): Boolean

    fun save(review: Review): Review

    fun findById(reviewId: Long): Optional<Review>
}
