package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.review.application.UserReviewService
import com.fastcampus.commerce.review.interfaces.request.UserReviewApiRequest
import com.fastcampus.commerce.review.interfaces.request.UserReviewApiResponse
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
class UserReviewController(
    private val userReviewService: UserReviewService,
) {
    @GetMapping("/reviews:byAuthor")
    fun getReviewsByAuthor(
        @ModelAttribute request: UserReviewApiRequest,
        pageable: Pageable,
    ): PagedData<UserReviewApiResponse> {
        val userId = 1L
        return PagedData.of(
            userReviewService.getReviewsByAuthor(request.toServiceRequest(userId), pageable)
                .map(UserReviewApiResponse::from),
        )
    }
}
