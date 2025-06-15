package com.fastcampus.commerce.payment.domain.repository

import com.fastcampus.commerce.payment.domain.entity.Payment
import java.util.Optional

interface PaymentRepository {
    fun findByOrderId(orderId: Long): Optional<Payment>

    fun findByPaymentNumber(paymentNumber: String): Optional<Payment>
}
