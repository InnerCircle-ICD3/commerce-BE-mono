package com.fastcampus.commerce.admin.review.application.response

import com.fastcampus.commerce.review.domain.model.ReviewInfo
import java.time.LocalDateTime

data class SearchReviewAdminResponse(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val adminReply: SearchReviewAdminReplyResponse? = null,
    val user: SearchReviewAdminAuthorResponse,
    val product: SearchReviewAdminProductResponse,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(reviewInfo: ReviewInfo) =
            SearchReviewAdminResponse(
                reviewId = reviewInfo.reviewId,
                rating = reviewInfo.rating,
                content = reviewInfo.content,
                adminReply = reviewInfo.adminReply?.let { SearchReviewAdminReplyResponse(it.content, it.createdAt) },
                user = SearchReviewAdminAuthorResponse(userId = reviewInfo.user.userId, nickname = reviewInfo.user.nickname),
                product = SearchReviewAdminProductResponse(
                    productId = reviewInfo.product.productId,
                    productName = reviewInfo.product.productName,
                ),
                createdAt = reviewInfo.createdAt,
            )
    }
}

data class SearchReviewAdminReplyResponse(
    val content: String,
    val createdAt: LocalDateTime,
)

data class SearchReviewAdminAuthorResponse(
    val userId: Long,
    val nickname: String,
)

data class SearchReviewAdminProductResponse(
    val productId: Long,
    val productName: String,
)
