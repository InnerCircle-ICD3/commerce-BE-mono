package com.fastcampus.commerce.review.interfaces.request

import com.fastcampus.commerce.review.application.request.RegisterReviewRequest
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RegisterReviewApiRequest(
    @field:NotBlank(message = "주문번호가 비어있습니다.")
    val orderNumber: String,
    @field:NotNull(message = "주문 항목이 비어있습니다.")
    val orderItemId: Long,
    @field:Min(value = 1, message = "별점은 1~5점 사이로 선택해주세요.")
    @field:Max(value = 5, message = "별점은 1~5점 사이로 선택해주세요.")
    val rating: Int,
    @field:NotBlank(message = "리뷰내용을 입력해주세요.")
    val content: String,
) {
    fun toServiceRequest(): RegisterReviewRequest =
        RegisterReviewRequest(
            orderNumber = orderNumber,
            orderItemId = orderItemId,
            rating = rating,
            content = content,
        )
}
