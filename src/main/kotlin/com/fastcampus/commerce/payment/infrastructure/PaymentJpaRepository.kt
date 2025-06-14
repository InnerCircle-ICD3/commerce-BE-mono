package com.fastcampus.commerce.payment.infrastructure

import com.fastcampus.commerce.payment.domain.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PaymentJpaRepository : JpaRepository<Payment, Long> {
    fun findByOrderId(orderId: Long): Optional<Payment>

    fun findByPaymentNumber(paymentNumber: String): Optional<Payment>
}
