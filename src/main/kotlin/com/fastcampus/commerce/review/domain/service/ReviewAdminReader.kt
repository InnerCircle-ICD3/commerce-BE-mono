package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.review.domain.model.AdminReply
import com.fastcampus.commerce.review.domain.model.ReviewAdminInfo
import com.fastcampus.commerce.review.domain.model.ReviewAuthor
import com.fastcampus.commerce.review.domain.model.ReviewProduct
import com.fastcampus.commerce.review.domain.model.SearchReviewAdminCondition
import com.fastcampus.commerce.review.domain.repository.ReviewAdminRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ReviewAdminReader(
    private val reviewAdminRepository: ReviewAdminRepository,
) {
    fun searchReviews(condition: SearchReviewAdminCondition, pageable: Pageable): Page<ReviewAdminInfo> {
        return reviewAdminRepository.searchReviews(condition, pageable).map {
            ReviewAdminInfo(
                reviewId = it.reviewId,
                rating = it.rating,
                content = it.content,
                adminReply = if (it.adminReplyContent != null && it.adminReplyCreatedAt != null) {
                    AdminReply(it.adminReplyContent, it.adminReplyCreatedAt)
                } else {
                    null
                },
                user = ReviewAuthor(it.userId, it.userNickname),
                product = ReviewProduct(it.productId, it.productName),
                createdAt = it.createdAt,
            )
        }
    }
}
