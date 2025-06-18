package com.fastcampus.commerce.payment.application

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.payment.domain.entity.Payment
import com.fastcampus.commerce.payment.domain.entity.PaymentStatus
import com.fastcampus.commerce.payment.domain.error.PaymentErrorCode
import com.fastcampus.commerce.payment.domain.model.PgPaymentInfo
import org.springframework.stereotype.Component

@Component
class PaymentValidator {
    fun validateProcessPayment(pgPaymentInfo: PgPaymentInfo, orderPayment: Order, payment: Payment) {
        if (orderPayment.status !== OrderStatus.WAITING_FOR_PAYMENT) {
            throw CoreException(PaymentErrorCode.ALREADY_PAID_ORDER)
        }
        if (payment.status !== PaymentStatus.WAITING) {
            throw CoreException(PaymentErrorCode.ALREADY_PAID)
        }
        // 현재 Mock이므로 임시 주석처리한다.
//        if (payment.amount !== pgPaymentInfo.amount) {
//            throw CoreException(PaymentErrorCode.PG_RESULT_NOT_MATCH_PAYMENT)
//        }
    }
}
