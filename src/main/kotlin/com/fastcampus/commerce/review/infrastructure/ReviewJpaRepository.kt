package com.fastcampus.commerce.review.infrastructure

import com.fastcampus.commerce.review.domain.entity.Review
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewJpaRepository : JpaRepository<Review, Long> {
    fun existsByUserIdAndOrderItemId(userId: Long, orderItemId: Long): Boolean
}
