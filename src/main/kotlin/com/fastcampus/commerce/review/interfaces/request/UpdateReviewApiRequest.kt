package com.fastcampus.commerce.review.interfaces.request

import com.fastcampus.commerce.review.application.request.UpdateReviewRequest
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class UpdateReviewApiRequest(
    @field:Min(value = 1, message = "별점은 1~5점 사이로 선택해주세요.")
    @field:Max(value = 5, message = "별점은 1~5점 사이로 선택해주세요.")
    val rating: Int,
    @field:NotBlank(message = "리뷰내용이 누락되었습니다.")
    val content: String,
) {
    fun toServiceRequest(): UpdateReviewRequest =
        UpdateReviewRequest(
            rating = rating,
            content = content,
        )
}
