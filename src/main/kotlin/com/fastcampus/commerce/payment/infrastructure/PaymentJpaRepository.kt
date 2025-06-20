package com.fastcampus.commerce.payment.infrastructure

import com.fastcampus.commerce.payment.domain.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface PaymentJpaRepository : JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId order by p.createdAt desc limit 1")
    fun findByOrderId(orderId: Long): Optional<Payment>

    fun findByPaymentNumber(paymentNumber: String): Optional<Payment>
}
