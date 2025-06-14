package com.fastcampus.commerce.payment.infrastructure

import com.fastcampus.commerce.payment.domain.entity.Payment
import com.fastcampus.commerce.payment.domain.repository.PaymentRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class PaymentRepositoryImpl(
    private val paymentJpaRepository: PaymentJpaRepository,
) : PaymentRepository {
    override fun findByOrderId(orderId: Long): Optional<Payment> {
        return paymentJpaRepository.findByOrderId(orderId)
    }

    override fun findByPaymentNumber(paymentNumber: String): Optional<Payment> {
        return paymentJpaRepository.findByPaymentNumber(paymentNumber)
    }
}
