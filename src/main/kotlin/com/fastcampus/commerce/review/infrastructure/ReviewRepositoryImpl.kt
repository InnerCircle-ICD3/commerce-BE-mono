package com.fastcampus.commerce.review.infrastructure

import com.fastcampus.commerce.review.domain.entity.Review
import com.fastcampus.commerce.review.domain.repository.ReviewRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class ReviewRepositoryImpl(
    private val reviewJpaRepository: ReviewJpaRepository,
) : ReviewRepository {
    override fun existsByUserIdAndOrderItemId(userId: Long, orderItemId: Long): Boolean {
        return reviewJpaRepository.existsByUserIdAndOrderItemId(userId, orderItemId)
    }

    override fun save(review: Review): Review {
        return reviewJpaRepository.save(review)
    }

    override fun findById(reviewId: Long): Optional<Review> {
        return reviewJpaRepository.findById(reviewId)
    }
}
