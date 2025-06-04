package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.review.application.ReviewCommandService
import com.fastcampus.commerce.review.interfaces.request.RegisterReviewApiRequest
import com.fastcampus.commerce.review.interfaces.response.RegisterReviewApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/reviews")
@RestController
class ReviewController(
    private val reviewCommandService: ReviewCommandService,
) {
    @PostMapping
    fun registerReview(
        @RequestBody request: RegisterReviewApiRequest,
    ): RegisterReviewApiResponse {
        val userId = 1L
        val reviewId = reviewCommandService.registerReview(userId, request.toServiceRequest())
        return RegisterReviewApiResponse(reviewId)
    }
}
