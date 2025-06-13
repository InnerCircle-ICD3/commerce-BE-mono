package com.fastcampus.commerce.review.domain.model

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class ReviewInfo
    @QueryProjection
    constructor(
        val reviewId: Long,
        val rating: Int,
        val content: String,
        val adminReply: AdminReply? = null,
        val user: ReviewAuthor,
        val product: ReviewProduct,
        val createdAt: LocalDateTime,
    ) {
        companion object {
            fun from(reviewInfoFlat: ReviewInfoFlat): ReviewInfo =
                ReviewInfo(
                    reviewId = reviewInfoFlat.reviewId,
                    rating = reviewInfoFlat.rating,
                    content = reviewInfoFlat.content,
                    adminReply = reviewInfoFlat.adminReplyContent?.let {
                        AdminReply(
                            it,
                            reviewInfoFlat.adminReplyCreatedAt!!,
                        )
                    },
                    user = ReviewAuthor(
                        userId = reviewInfoFlat.userId,
                        nickname = reviewInfoFlat.userNickname,
                    ),
                    product = ReviewProduct(
                        productId = reviewInfoFlat.productId,
                        productName = reviewInfoFlat.productName,
                    ),
                    createdAt = reviewInfoFlat.createdAt,
                )
        }
    }
