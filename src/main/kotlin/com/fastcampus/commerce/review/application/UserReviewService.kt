package com.fastcampus.commerce.review.application

import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.review.application.request.UserReviewRequest
import com.fastcampus.commerce.review.application.response.UserReviewResponse
import com.fastcampus.commerce.review.domain.repository.UserReviewRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserReviewService(
    private val timeProvider: TimeProvider,
    private val userReviewRepository: UserReviewRepository,
) {
    @Transactional(readOnly = true)
    fun getReviewsByAuthor(request: UserReviewRequest, pageable: Pageable): Page<UserReviewResponse> {
        val condition = request.toCondition(timeProvider.now())
        return userReviewRepository.getReviewsBy(condition, pageable)
            .map(UserReviewResponse::from)
    }
}
