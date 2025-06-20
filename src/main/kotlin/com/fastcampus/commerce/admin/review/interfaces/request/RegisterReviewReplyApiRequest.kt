package com.fastcampus.commerce.admin.review.interfaces.request

import jakarta.validation.constraints.NotBlank

data class RegisterReviewReplyApiRequest(
    @field:NotBlank(message = "답글 내용을 입력해주세요.")
    val content: String,
)
