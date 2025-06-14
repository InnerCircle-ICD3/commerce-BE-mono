package com.fastcampus.commerce.admin.payment.application

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.order.application.OrderPaymentService
import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.payment.domain.entity.Payment
import com.fastcampus.commerce.payment.domain.entity.PaymentMethod
import com.fastcampus.commerce.payment.domain.entity.PaymentStatus
import com.fastcampus.commerce.payment.domain.error.PaymentErrorCode
import com.fastcampus.commerce.payment.domain.service.PaymentReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class AdminPaymentServiceTest : FunSpec(
    {
        val timeProvider = mockk<TimeProvider>()
        val paymentReader = mockk<PaymentReader>()
        val orderPaymentService = mockk<OrderPaymentService>()

        val adminPaymentService = AdminPaymentService(
            timeProvider = timeProvider,
            paymentReader = paymentReader,
            orderPaymentService = orderPaymentService,
        )

        beforeTest {
            clearMocks(timeProvider, paymentReader, orderPaymentService)
        }

        context("refundApprove") {
            val adminId = 1L
            test("정상적으로 환불을 승인한다") {
                // given
                val paymentNumber = "PAY-20241214-001"
                val orderId = 1L
                val now = LocalDateTime.of(2024, 12, 14, 18, 0, 0)

                val payment = Payment(
                    paymentNumber = paymentNumber,
                    orderId = orderId,
                    userId = 100L,
                    amount = 50000,
                    paymentMethod = PaymentMethod.TOSS_PAY,
                    status = PaymentStatus.COMPLETED,
                ).apply {
                    id = 1L
                    transactionId = "TRANSACTION_ID"
                }

                val order = Order(
                    orderNumber = "ORD-20241214-001",
                    userId = 100L,
                    totalAmount = 50000,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.REFUND_REQUESTED,
                ).apply {
                    id = orderId
                }

                every { paymentReader.getByPaymentNumber(paymentNumber) } returns payment
                every { orderPaymentService.getOrderByOrderId(orderId) } returns order
                every { timeProvider.now() } returns now

                // when
                adminPaymentService.refundApprove(adminId, paymentNumber)

                // then
                payment.status shouldBe PaymentStatus.REFUNDED
                order.status shouldBe OrderStatus.REFUNDED
                order.refundedAt shouldBe now

                verify(exactly = 1) { paymentReader.getByPaymentNumber(paymentNumber) }
                verify(exactly = 1) { orderPaymentService.getOrderByOrderId(orderId) }
                verify(exactly = 1) { timeProvider.now() }
            }

            test("존재하지 않는 결제번호로 환불 승인시 예외가 발생한다") {
                // given
                val nonExistentPaymentNumber = "PAY-99999999-999"

                every { paymentReader.getByPaymentNumber(nonExistentPaymentNumber) } throws
                    CoreException(PaymentErrorCode.PAYMENT_NOT_FOUND)

                shouldThrow<CoreException> {
                    adminPaymentService.refundApprove(adminId, nonExistentPaymentNumber)
                }.errorCode shouldBe PaymentErrorCode.PAYMENT_NOT_FOUND

                verify(exactly = 1) { paymentReader.getByPaymentNumber(nonExistentPaymentNumber) }
                verify(exactly = 0) { orderPaymentService.getOrderByOrderId(any<Long>()) }
                verify(exactly = 0) { timeProvider.now() }
            }

            test("취소된 결제에 대한 환불 승인시 상태가 변경되지 않는다") {
                // given
                val paymentNumber = "PAY-20241214-005"
                val orderId = 5L
                val now = LocalDateTime.of(2024, 12, 14, 21, 0, 0)

                val payment = Payment(
                    paymentNumber = paymentNumber,
                    orderId = orderId,
                    userId = 500L,
                    amount = 25000,
                    paymentMethod = PaymentMethod.MOCK,
                    status = PaymentStatus.CANCELLED, // 이미 취소됨
                ).apply {
                    id = 5L
                    transactionId = "KAKAO_TX_55555"
                }

                val order = Order(
                    orderNumber = "ORD-20241214-005",
                    userId = 500L,
                    totalAmount = 25000,
                    recipientName = "최지우",
                    recipientPhone = "010-9999-0000",
                    zipCode = "22222",
                    address1 = "서울시 용산구",
                    status = OrderStatus.CANCELLED, // 이미 취소됨
                ).apply {
                    id = orderId
                    canceledAt = LocalDateTime.of(2024, 12, 13, 15, 0, 0)
                }

                every { paymentReader.getByPaymentNumber(paymentNumber) } returns payment
                every { orderPaymentService.getOrderByOrderId(orderId) } returns order
                every { timeProvider.now() } returns now

                shouldThrow<CoreException> {
                    adminPaymentService.refundApprove(adminId, paymentNumber)
                }.errorCode shouldBe OrderErrorCode.NOT_REFUND_REQUESTED

                verify(exactly = 1) { paymentReader.getByPaymentNumber(paymentNumber) }
                verify(exactly = 1) { orderPaymentService.getOrderByOrderId(orderId) }
                verify(exactly = 1) { timeProvider.now() }
            }
        }
    },
)
