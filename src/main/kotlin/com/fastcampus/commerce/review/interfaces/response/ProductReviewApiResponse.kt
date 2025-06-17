package com.fastcampus.commerce.review.interfaces.response

import com.fastcampus.commerce.review.application.response.AdminReplyResponse
import com.fastcampus.commerce.review.application.response.ProductReviewResponse
import java.time.LocalDateTime

data class ProductReviewApiResponse(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val createdAt: LocalDateTime,
    val adminReply: AdminReplyApiResponse? = null,
    val user: ProductReviewUserApiResponse,
) {
    companion object {
        fun from(it: ProductReviewResponse): ProductReviewApiResponse =
            ProductReviewApiResponse(
                reviewId = it.reviewId,
                rating = it.rating,
                content = it.content,
                createdAt = it.createdAt,
                adminReply = it.adminReply?.let(AdminReplyApiResponse::from),
                user = ProductReviewUserApiResponse(it.user.userId, it.user.nickname),
            )
    }
}

data class AdminReplyApiResponse(
    val content: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(adminReply: AdminReplyResponse) =
            AdminReplyApiResponse(
                content = adminReply.content,
                createdAt = adminReply.createdAt,
            )
    }
}

data class ProductReviewUserApiResponse(
    val userId: String,
    val nickname: String,
)
