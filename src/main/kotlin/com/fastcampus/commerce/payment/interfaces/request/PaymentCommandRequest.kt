package com.fastcampus.commerce.payment.interfaces.request

import jakarta.validation.constraints.NotBlank

data class PaymentCommandRequest(
    @field:NotBlank(message = "주문번호가 누락되었습니다.")
    val orderNumber: String?= null,
)
