package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.interfaces.dto.AppendReviewRequest
import com.fastcampus.commerce.review.interfaces.dto.AppendReviewResponse
import com.fastcampus.commerce.review.interfaces.dto.DeleteReviewResponse
import com.fastcampus.commerce.review.interfaces.dto.UpdateReviewRequest
import com.fastcampus.commerce.review.interfaces.dto.UpdateReviewResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/reviews")
@RestController
class ReviewController {
    @PostMapping
    fun appendReview(
        @RequestBody request: AppendReviewRequest,
    ): AppendReviewResponse {
        if (request.content.isNullOrBlank()) {
            throw CoreException(ReviewErrorCode.REVIEW_CONTENT_EMPTY)
        }
        return AppendReviewResponse(1)
    }

    @PutMapping("/{reviewId}")
    fun updateReview(
        @PathVariable reviewId: Long,
        @RequestBody request: UpdateReviewRequest,
    ): UpdateReviewResponse {
        if (request.content.isNullOrBlank()) {
            throw CoreException(ReviewErrorCode.REVIEW_CONTENT_EMPTY)
        }
        return UpdateReviewResponse(1)
    }

    @DeleteMapping("/{reviewId}")
    fun updateReview(
        @PathVariable reviewId: Long,
    ): DeleteReviewResponse {
        if (reviewId != 1L) {
            throw CoreException(ReviewErrorCode.REVIEW_CONTENT_EMPTY)
        }
        return DeleteReviewResponse()
    }
}
