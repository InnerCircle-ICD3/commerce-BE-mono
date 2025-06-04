package com.fastcampus.commerce.review.domain.model

import com.fastcampus.commerce.review.domain.entity.Review
import java.time.LocalDateTime

data class ReviewRegister(
    val userId: Long,
    val orderItemId: Long,
    val productId: Long,
    val rating: Int,
    val content: String,
    val deliveredAt: LocalDateTime? = null,
) {
    fun toReview(): Review =
        Review(
            userId = userId,
            orderItemId = orderItemId,
            productId = productId,
            rating = rating,
            content = content,
        )
}
