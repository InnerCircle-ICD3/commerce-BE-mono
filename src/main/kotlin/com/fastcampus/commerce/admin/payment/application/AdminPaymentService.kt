package com.fastcampus.commerce.admin.payment.application

import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.order.application.payment.OrderPaymentService
import com.fastcampus.commerce.payment.domain.service.PaymentReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminPaymentService(
    private val timeProvider: TimeProvider,
    private val paymentReader: PaymentReader,
    private val orderPaymentService: OrderPaymentService,
) {
    @Transactional(readOnly = false)
    fun refundApprove(adminId: Long, paymentNumber: String) {
        val payment = paymentReader.getByPaymentNumber(paymentNumber)
        val order = orderPaymentService.getOrderByOrderId(payment.orderId)
        val refundAt = timeProvider.now()
        payment.refundApprove(refundAt)
        order.refundApprove(refundAt)
    }

    @Transactional(readOnly = false)
    fun refundReject(adminId: Long, paymentNumber: String) {
        val payment = paymentReader.getByPaymentNumber(paymentNumber)
        val order = orderPaymentService.getOrderByOrderId(payment.orderId)
        val rejectedAt = timeProvider.now()
        order.refundReject(rejectedAt)
    }
}
