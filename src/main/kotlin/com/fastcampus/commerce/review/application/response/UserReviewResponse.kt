package com.fastcampus.commerce.review.application.response

import com.fastcampus.commerce.review.domain.model.ReviewInfoFlat
import java.time.LocalDateTime

data class UserReviewResponse(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val adminReply: UserReviewAdminReplyResponse? = null,
    val product: UserReviewProductResponse,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(info: ReviewInfoFlat): UserReviewResponse {
            return UserReviewResponse(
                reviewId = info.reviewId,
                rating = info.rating,
                content = info.content,
                adminReply = info.adminReplyContent?.let {
                    UserReviewAdminReplyResponse(
                        it,
                        info.adminReplyCreatedAt!!,
                    )
                },
                product = UserReviewProductResponse(
                    info.productId,
                    info.productName,
                    info.productThumbnail,
                ),
                createdAt = info.createdAt,
            )
        }
    }
}

data class UserReviewAdminReplyResponse(
    val content: String,
    val createdAt: LocalDateTime,
)

data class UserReviewProductResponse(
    val productId: Long,
    val productName: String,
    val productThumbnail: String,
)
