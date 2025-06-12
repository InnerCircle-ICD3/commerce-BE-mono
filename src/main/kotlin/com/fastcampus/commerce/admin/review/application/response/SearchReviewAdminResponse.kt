package com.fastcampus.commerce.admin.review.application.response

import com.fastcampus.commerce.review.domain.model.ReviewAdminInfo
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
        fun from(reviewAdminInfo: ReviewAdminInfo) =
            SearchReviewAdminResponse(
                reviewId = reviewAdminInfo.reviewId,
                rating = reviewAdminInfo.rating,
                content = reviewAdminInfo.content,
                adminReply = reviewAdminInfo.adminReply?.let { SearchReviewAdminReplyResponse(it.content, it.createdAt) },
                user = SearchReviewAdminAuthorResponse(userId = reviewAdminInfo.user.userId, nickname = reviewAdminInfo.user.nickname),
                product = SearchReviewAdminProductResponse(
                    productId = reviewAdminInfo.product.productId,
                    productName = reviewAdminInfo.product.productName,
                ),
                createdAt = reviewAdminInfo.createdAt,
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
