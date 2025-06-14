package com.fastcampus.commerce.admin.review.application

import com.fastcampus.commerce.admin.review.application.request.SearchReviewAdminRequest
import com.fastcampus.commerce.admin.review.application.response.SearchReviewAdminResponse
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.review.domain.service.ReviewAdminReader
import com.fastcampus.commerce.review.domain.service.ReviewAdminStore
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminReviewService(
    private val timeProvider: TimeProvider,
    private val reviewAdminReader: ReviewAdminReader,
    private val reviewAdminStore: ReviewAdminStore,
) {
    @Transactional(readOnly = true)
    fun search(request: SearchReviewAdminRequest, pageable: Pageable): Page<SearchReviewAdminResponse> {
        val now = timeProvider.now()
        val condition = request.toCondition(now)
        val searchReviews = reviewAdminReader.searchReviews(condition, pageable)
        return searchReviews.map(SearchReviewAdminResponse.Companion::from)
    }

    @Transactional
    fun registerReply(adminId: Long, reviewId: Long, content: String): Long {
        val reviewAdminInfo = reviewAdminReader.getReview(reviewId)
        val reviewReply = reviewAdminStore.registerReply(adminId, reviewAdminInfo, content)
        return reviewReply.id!!
    }

    @Transactional
    fun updateReply(adminId: Long, replyId: Long, content: String) {
        val reply = reviewAdminReader.getReply(replyId)
        reply.updateContent(adminId, content)
    }

    @Transactional
    fun deleteReply(adminId: Long, replyId: Long) {
        val reply = reviewAdminReader.getReply(replyId)
        reviewAdminStore.deleteReply(adminId, reply)
    }
}
