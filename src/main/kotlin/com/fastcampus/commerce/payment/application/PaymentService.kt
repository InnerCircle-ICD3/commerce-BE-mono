package com.fastcampus.commerce.payment.application

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.order.application.payment.OrderPaymentService
import com.fastcampus.commerce.payment.application.request.PaymentProcessRequest
import com.fastcampus.commerce.payment.application.response.PaymentProcessResponse
import com.fastcampus.commerce.payment.domain.error.PaymentErrorCode
import com.fastcampus.commerce.payment.domain.service.PaymentReader
import com.fastcampus.commerce.payment.domain.service.PgClient
import com.fastcampus.commerce.product.domain.service.ProductStore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val timeProvider: TimeProvider,
    private val pgClient: PgClient,
    private val orderPaymentService: OrderPaymentService,
    private val paymentReader: PaymentReader,
    private val paymentValidator: PaymentValidator,
    private val productStore: ProductStore,
) {
    @Transactional(readOnly = false)
    fun processPayment(request: PaymentProcessRequest): PaymentProcessResponse {
        val paymentInfo = pgClient.getPaymentInfo(request.transactionId)
            ?: throw CoreException(PaymentErrorCode.PG_RESULT_NOT_FOUND)

        val order = orderPaymentService.getOrderByOrderNumber(request.orderNumber)
        val payment = paymentReader.getByOrderId(order.id!!)
        paymentValidator.validateProcessPayment(paymentInfo, order, payment)

        val paidAt = timeProvider.now()
        payment.paid(paidAt)

        try {
            val orderProducts = orderPaymentService.getOrderProducts(order.id!!)
            orderProducts.forEach { productStore.decreaseQuantityByProductId(it.productId, it.quantity) }
            order.paid(paidAt)
            return PaymentProcessResponse(payment.paymentNumber)
        } catch (e: CoreException) {
            payment.fail(paidAt, e.message)
            order.fail(paidAt)
            pgClient.refund(payment.transactionId!!, payment.amount)
            throw CoreException(PaymentErrorCode.QUANTITY_NOT_ENOUGH)
        }
    }

    @Transactional(readOnly = false)
    fun cancelPayment(userId: Long, orderNumber: String) {
        val order = orderPaymentService.getOrderByOrderNumber(orderNumber)
        order.verifyCancelable(userId)
        val payment = paymentReader.getByOrderId(order.id!!)
        if (payment.transactionId == null) {
            throw CoreException(PaymentErrorCode.TRANSACTION_ID_EMPTY)
        }
        val cancelledAt = timeProvider.now()
        payment.cancel(cancelledAt)
        val orderProducts = orderPaymentService.getOrderProducts(order.id!!)
        orderProducts.forEach { productStore.increaseQuantityByProductId(it.productId, it.quantity) }
        order.cancel(cancelledAt)
        pgClient.refund(payment.transactionId!!, payment.amount)
    }

    @Transactional(readOnly = false)
    fun refundRequestPayment(userId: Long, orderNumber: String) {
        val order = orderPaymentService.getOrderByOrderNumber(orderNumber)
        order.refundRequest(userId, timeProvider.now())
    }
}
