package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.review.application.ReviewCommandService
import com.fastcampus.commerce.review.interfaces.request.RegisterReviewApiRequest
import com.fastcampus.commerce.review.interfaces.request.UpdateReviewApiRequest
import com.fastcampus.commerce.review.interfaces.response.DeleteReviewApiResponse
import com.fastcampus.commerce.review.interfaces.response.RegisterReviewApiResponse
import com.fastcampus.commerce.review.interfaces.response.UpdateReviewApiResponse
import com.fastcampus.commerce.user.domain.enums.UserRole
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RequestMapping("/reviews")
@RestController
class ReviewController(
    private val reviewCommandService: ReviewCommandService,
) {
    @PostMapping
    fun registerReview(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @Valid @RequestBody request: RegisterReviewApiRequest,
    ): RegisterReviewApiResponse {
        val reviewId = reviewCommandService.registerReview(user.id, request.toServiceRequest())
        return RegisterReviewApiResponse(reviewId)
    }

    @PutMapping("/{reviewId}")
    fun updateReview(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @PathVariable reviewId: Long,
        @Valid @RequestBody request: UpdateReviewApiRequest,
    ): UpdateReviewApiResponse {
        reviewCommandService.updateReview(user.id, reviewId, request.toServiceRequest())
        return UpdateReviewApiResponse(reviewId)
    }

    @DeleteMapping("/{reviewId}")
    fun deleteReview(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @PathVariable reviewId: Long,
    ): DeleteReviewApiResponse {
        reviewCommandService.deleteReview(user.id, reviewId)
        return DeleteReviewApiResponse()
    }
}
