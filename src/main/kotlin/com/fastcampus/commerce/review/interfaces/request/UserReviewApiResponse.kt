package com.fastcampus.commerce.review.interfaces.request

import com.fastcampus.commerce.review.application.response.UserReviewResponse
import java.time.LocalDateTime

data class UserReviewApiResponse(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val adminReply: UserReviewAdminReplyApiResponse? = null,
    val product: UserReviewProductApiResponse,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(response: UserReviewResponse) =
            UserReviewApiResponse(
                reviewId = response.reviewId,
                rating = response.rating,
                content = response.content,
                adminReply = response.adminReply?.let {
                    UserReviewAdminReplyApiResponse(
                        it.content,
                        it.createdAt,
                    )
                },
                product = UserReviewProductApiResponse(
                    response.product.productId,
                    response.product.productName,
                    response.product.productThumbnail,
                ),
                createdAt = response.createdAt,
            )
    }
}

data class UserReviewAdminReplyApiResponse(
    val content: String,
    val createdAt: LocalDateTime,
)

data class UserReviewProductApiResponse(
    val productId: Long,
    val productName: String,
    val productThumbnail: String,
)
