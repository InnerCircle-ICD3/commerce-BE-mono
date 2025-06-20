package com.fastcampus.commerce.admin.payment.interfaces

import com.fastcampus.commerce.admin.payment.application.AdminPaymentService
import com.fastcampus.commerce.admin.payment.interfaces.response.AdminPaymentApiResponse
import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.user.domain.enums.UserRole
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminPaymentController(
    private val adminPaymentService: AdminPaymentService,
) {
    @PostMapping("/admin/payments/refund/approve")
    fun refundApprove(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @RequestBody paymentNumber: String,
    ): AdminPaymentApiResponse {
        adminPaymentService.refundApprove(admin.id, paymentNumber)
        return AdminPaymentApiResponse()
    }

    @PostMapping("/admin/payments/refund/reject")
    fun refundReject(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @RequestBody paymentNumber: String,
    ): AdminPaymentApiResponse {
        adminPaymentService.refundReject(admin.id, paymentNumber)
        return AdminPaymentApiResponse()
    }
}
