package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.entity.ReviewReply
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.domain.model.ReviewInfo
import com.fastcampus.commerce.review.domain.repository.ReviewAdminRepository
import org.springframework.stereotype.Component

@Component
class ReviewAdminStore(
    private val reviewAdminRepository: ReviewAdminRepository,
) {
    fun registerReply(replierId: Long, reviewInfo: ReviewInfo, content: String): ReviewReply {
        if (reviewInfo.adminReply != null) {
            throw CoreException(ReviewErrorCode.REPLY_EXISTS)
        }
        return reviewAdminRepository.registerReply(
            ReviewReply(
                reviewId = reviewInfo.reviewId,
                replierId = replierId,
                content = content,
            ),
        )
    }

    fun deleteReply(adminId: Long, reply: ReviewReply) {
        reviewAdminRepository.deleteReply(reply)
    }
}
