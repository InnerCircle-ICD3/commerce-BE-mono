package com.fastcampus.commerce.payment.interfaces.request

import com.fastcampus.commerce.payment.application.request.PaymentProcessRequest
import jakarta.validation.constraints.NotBlank

data class PaymentApiRequest(
    @field:NotBlank(message = "주문 번호가 누락되었습니다.")
    val orderNumber: String,
    @field:NotBlank(message = "결제 ID가 누락되었습니다.")
    val transactionId: String,
) {
    fun toServiceRequest() =
        PaymentProcessRequest(
            orderNumber = orderNumber,
            transactionId = transactionId,
        )
}
