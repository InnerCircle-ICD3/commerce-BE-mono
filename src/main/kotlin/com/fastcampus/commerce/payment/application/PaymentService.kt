package com.fastcampus.commerce.payment.application

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.order.application.OrderPaymentService
import com.fastcampus.commerce.payment.application.request.PaymentProcessRequest
import com.fastcampus.commerce.payment.application.response.PaymentProcessResponse
import com.fastcampus.commerce.payment.domain.error.PaymentErrorCode
import com.fastcampus.commerce.payment.domain.service.PaymentReader
import com.fastcampus.commerce.payment.domain.service.PgClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val timeProvider: TimeProvider,
    private val pgClient: PgClient,
    private val orderPaymentService: OrderPaymentService,
    private val paymentReader: PaymentReader,
    private val paymentValidator: PaymentValidator,
) {
    @Transactional(readOnly = false)
    fun processPayment(request: PaymentProcessRequest): PaymentProcessResponse {
        val paymentInfo = pgClient.getPaymentInfo(request.transactionId)
            ?: throw CoreException(PaymentErrorCode.PG_RESULT_NOT_FOUND)

        val order = orderPaymentService.getOrder(request.orderNumber)
        val payment = paymentReader.getByOrderId(order.id!!)
        paymentValidator.validateProcessPayment(paymentInfo, order, payment)
        val paidAt = timeProvider.now()
        payment.paid(paidAt)
        order.paid(paidAt)
        return PaymentProcessResponse(payment.paymentNumber)
    }
}
