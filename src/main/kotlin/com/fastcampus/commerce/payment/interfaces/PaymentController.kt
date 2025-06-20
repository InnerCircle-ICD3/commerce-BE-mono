package com.fastcampus.commerce.payment.interfaces

import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.payment.application.PaymentService
import com.fastcampus.commerce.payment.interfaces.request.PaymentApiRequest
import com.fastcampus.commerce.payment.interfaces.request.PaymentCommandRequest
import com.fastcampus.commerce.payment.interfaces.response.CancelPaymentApiResponse
import com.fastcampus.commerce.payment.interfaces.response.PaymentApiResponse
import com.fastcampus.commerce.user.domain.enums.UserRole
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
        @WithRoles([UserRole.USER]) user: LoginUser,
        @RequestBody request: PaymentApiRequest,
    ): PaymentApiResponse {
        val response = paymentService.processPayment(request.toServiceRequest())
        return PaymentApiResponse(response.paymentNumber)
    }

    @PostMapping("/cancel")
    fun cancelPayment(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @RequestBody request: PaymentCommandRequest,
    ): CancelPaymentApiResponse {
        paymentService.cancelPayment(user.id, request.orderNumber)
        return CancelPaymentApiResponse()
    }

    @PostMapping("/refund")
    fun refundPayment(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @RequestBody request: PaymentCommandRequest,
    ): CancelPaymentApiResponse {
        paymentService.refundRequestPayment(user.id, request.orderNumber)
        return CancelPaymentApiResponse()
    }
}
