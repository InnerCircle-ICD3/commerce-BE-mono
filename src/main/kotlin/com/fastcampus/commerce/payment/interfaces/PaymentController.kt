package com.fastcampus.commerce.payment.interfaces

import com.fastcampus.commerce.payment.application.PaymentService
import com.fastcampus.commerce.payment.interfaces.request.PaymentApiRequest
import com.fastcampus.commerce.payment.interfaces.response.CancelPaymentApiResponse
import com.fastcampus.commerce.payment.interfaces.response.PaymentApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/payments")
@RestController
class PaymentController(
    private val paymentService: PaymentService,
) {
    @PostMapping
    fun verifyPayment(
        @RequestBody request: PaymentApiRequest,
    ): PaymentApiResponse {
        val response = paymentService.processPayment(request.toServiceRequest())
        return PaymentApiResponse(response.paymentNumber)
    }

    @PostMapping("/cancel")
    fun cancelPayment(
        @RequestBody orderNumber: String,
    ): CancelPaymentApiResponse {
        val userId = 1L
        paymentService.cancelPayment(userId, orderNumber)
        return CancelPaymentApiResponse()
    }

    @PostMapping("/refund")
    fun refundPayment(
        @RequestBody orderNumber: String,
    ): CancelPaymentApiResponse {
        val userId = 1L
        paymentService.refundRequestPayment(userId, orderNumber)
        return CancelPaymentApiResponse()
    }
}
