package com.fastcampus.commerce.review.application.response

import com.fastcampus.commerce.review.domain.model.AdminReply
import com.fastcampus.commerce.review.domain.model.ProductReviewFlat
import java.time.LocalDateTime

data class ProductReviewResponse(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val createdAt: LocalDateTime,
    val adminReply: AdminReplyResponse? = null,
) {
    companion object {
        fun from(productReview: ProductReviewFlat): ProductReviewResponse =
            ProductReviewResponse(
                reviewId = productReview.reviewId,
                rating = productReview.rating,
                content = productReview.content,
                createdAt = productReview.createdAt,
                adminReply = if (productReview.replyContent == null || productReview.replyCreatedAt == null) {
                    null
                } else {
                    AdminReplyResponse(
                        productReview.replyContent,
                        productReview.replyCreatedAt,
                    )
                },
            )
    }
}

data class AdminReplyResponse(
    val content: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(adminReply: AdminReply): AdminReplyResponse =
            AdminReplyResponse(
                content = adminReply.content!!,
                createdAt = adminReply.createdAt!!,
            )
    }
}
