package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.entity.ReviewReply
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.domain.model.ReviewAdminInfo
import com.fastcampus.commerce.review.domain.repository.ReviewAdminRepository
import org.springframework.stereotype.Component

@Component
class ReviewAdminStore(
    private val reviewAdminRepository: ReviewAdminRepository,
) {
    fun registerReply(replierId: Long, reviewAdminInfo: ReviewAdminInfo, content: String): ReviewReply {
        if (reviewAdminInfo.adminReply != null) {
            throw CoreException(ReviewErrorCode.REPLY_EXISTS)
        }
        return reviewAdminRepository.registerReply(
            ReviewReply(
                reviewId = reviewAdminInfo.reviewId,
                replierId = replierId,
                content = content,
            ),
        )
    }
}
