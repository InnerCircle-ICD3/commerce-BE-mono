package com.fastcampus.commerce.payment.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.payment.domain.entity.Payment
import com.fastcampus.commerce.payment.domain.error.PaymentErrorCode
import com.fastcampus.commerce.payment.domain.repository.PaymentRepository
import org.springframework.stereotype.Component

@Component
class PaymentReader(
    private val paymentRepository: PaymentRepository,
) {
    fun getByOrderId(orderId: Long): Payment {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow { CoreException(PaymentErrorCode.PAYMENT_NOT_FOUND) }
    }
}
