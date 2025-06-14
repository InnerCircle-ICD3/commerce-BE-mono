package com.fastcampus.commerce.payment.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.payment.domain.entity.Payment
import com.fastcampus.commerce.payment.domain.entity.PaymentMethod
import com.fastcampus.commerce.payment.domain.entity.PaymentStatus
import com.fastcampus.commerce.payment.domain.error.PaymentErrorCode
import com.fastcampus.commerce.payment.domain.repository.PaymentRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class PaymentReaderTest : FunSpec(
    {
        val paymentRepository = mockk<PaymentRepository>()
        val paymentReader = PaymentReader(
            paymentRepository = paymentRepository,
        )

        beforeTest {
            clearMocks(paymentRepository)
        }

        context("getByPaymentNumber") {
            test("주문 아이디로 결제를 조회할 수 있다") {
                val paymentNumber = "PAY-20241214-001"
                val orderId = 1L
                val expectedPayment = Payment(
                    paymentNumber = paymentNumber,
                    orderId = orderId,
                    userId = 100L,
                    amount = 50000,
                    paymentMethod = PaymentMethod.TOSS_PAY,
                    status = PaymentStatus.COMPLETED,
                ).apply {
                    id = 1L
                    transactionId = "TOSS-TX-12345"
                }

                every { paymentRepository.findByOrderId(orderId) } returns Optional.of(expectedPayment)

                val result = paymentReader.getByOrderId(orderId)

                result shouldBe expectedPayment
                verify(exactly = 1) { paymentRepository.findByOrderId(orderId) }
            }

            test("주문 아이디로 결제를 조회할 때 결제가 없으면 PAYMENT_NOT_FOUND 예외가 발생한다") {
                val orderId = 1L
                every { paymentRepository.findByOrderId(orderId) } returns Optional.empty()

                shouldThrow<CoreException> {
                    paymentReader.getByOrderId(orderId)
                }.errorCode shouldBe PaymentErrorCode.PAYMENT_NOT_FOUND
            }
        }
    },
)
