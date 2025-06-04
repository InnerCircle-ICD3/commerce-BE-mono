package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.review.application.ReviewCommandService
import com.fastcampus.commerce.review.interfaces.request.RegisterReviewApiRequest
import com.fastcampus.commerce.review.interfaces.request.UpdateReviewApiRequest
import com.fastcampus.commerce.review.interfaces.response.DeleteReviewApiResponse
import com.fastcampus.commerce.review.interfaces.response.RegisterReviewApiResponse
import com.fastcampus.commerce.review.interfaces.response.UpdateReviewApiResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @PutMapping("/{reviewId}")
    fun updateReview(
        @PathVariable reviewId: Long,
        @RequestBody request: UpdateReviewApiRequest,
    ): UpdateReviewApiResponse {
        val userId = 1L
        reviewCommandService.updateReview(userId, reviewId, request.toServiceRequest())
        return UpdateReviewApiResponse(reviewId)
    }

    @DeleteMapping("/{reviewId}")
    fun updateReview(
        @PathVariable reviewId: Long,
    ): DeleteReviewApiResponse {
        val userId = 1L
        reviewCommandService.deleteReview(userId, reviewId)
        return DeleteReviewApiResponse()
    }
}
