package com.fastcampus.commerce.review.domain.model

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class ReviewAdminInfo
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
            fun from(reviewAdminInfoFlat: ReviewAdminInfoFlat): ReviewAdminInfo =
                ReviewAdminInfo(
                    reviewId = reviewAdminInfoFlat.reviewId,
                    rating = reviewAdminInfoFlat.rating,
                    content = reviewAdminInfoFlat.content,
                    adminReply = reviewAdminInfoFlat.adminReplyContent?.let {
                        AdminReply(
                            it,
                            reviewAdminInfoFlat.adminReplyCreatedAt!!,
                        )
                    },
                    user = ReviewAuthor(
                        userId = reviewAdminInfoFlat.userId,
                        nickname = reviewAdminInfoFlat.userNickname,
                    ),
                    product = ReviewProduct(
                        productId = reviewAdminInfoFlat.productId,
                        productName = reviewAdminInfoFlat.productName,
                    ),
                    createdAt = reviewAdminInfoFlat.createdAt,
                )
        }
    }
