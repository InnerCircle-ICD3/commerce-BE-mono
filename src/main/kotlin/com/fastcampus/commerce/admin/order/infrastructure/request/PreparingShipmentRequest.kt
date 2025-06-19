package com.fastcampus.commerce.admin.order.infrastructure.request

import jakarta.validation.constraints.NotBlank

data class PreparingShipmentRequest(
    @field:NotBlank(message = "운송장 번호를 입력해주세요.")
    val trackingNumber: String,
)
