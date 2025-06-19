package com.fastcampus.commerce.user.api.controller.request

import jakarta.validation.constraints.NotBlank

data class UpdateMyInfoRequest(
    @field:NotBlank(message = "변경할 닉네임을 입력해주세요.")
    val nickname: String,
)
