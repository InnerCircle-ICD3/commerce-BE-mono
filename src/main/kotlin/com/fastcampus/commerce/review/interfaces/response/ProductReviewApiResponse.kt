package com.fastcampus.commerce.review.interfaces.response

import com.fastcampus.commerce.review.application.response.AdminReplyResponse
import com.fastcampus.commerce.review.application.response.ProductReviewResponse
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ProductReviewApiResponse(
    val reviewId: Long,
    val rating: Int,
    val content: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    val createdAt: LocalDateTime,
    val adminReply: AdminReplyApiResponse? = null,
) {
    companion object {
        fun from(it: ProductReviewResponse): ProductReviewApiResponse =
            ProductReviewApiResponse(
                reviewId = it.reviewId,
                rating = it.rating,
                content = it.content,
                createdAt = it.createdAt,
                adminReply = it.adminReply?.let(AdminReplyApiResponse::from),
            )
    }
}

data class AdminReplyApiResponse(
    val content: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
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
