package com.fastcampus.commerce.admin.review.interfaces.response

import com.fastcampus.commerce.admin.review.application.response.SearchReviewAdminResponse
import java.time.LocalDateTime

data class SearchReviewAdminApiResponse(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    val adminReply: SearchReviewAdminReplyApiResponse? = null,
    val user: SearchReviewAdminAuthorApiResponse,
    val product: SearchReviewAdminProductApiResponse,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(response: SearchReviewAdminResponse) =
            SearchReviewAdminApiResponse(
                reviewId = response.reviewId,
                rating = response.rating,
                content = response.content,
                adminReply = response.adminReply?.let { SearchReviewAdminReplyApiResponse(it.content, it.createdAt) },
                user = SearchReviewAdminAuthorApiResponse(userId = response.user.userId, nickname = response.user.nickname),
                product = SearchReviewAdminProductApiResponse(
                    productId = response.product.productId,
                    productName = response.product.productName,
                ),
                createdAt = response.createdAt,
            )
    }
}

data class SearchReviewAdminReplyApiResponse(
    val content: String,
    val createdAt: LocalDateTime,
)

data class SearchReviewAdminAuthorApiResponse(
    val userId: Long,
    val nickname: String,
)

data class SearchReviewAdminProductApiResponse(
    val productId: Long,
    val productName: String,
)
