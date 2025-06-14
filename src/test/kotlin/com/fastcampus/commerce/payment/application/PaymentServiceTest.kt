package com.fastcampus.commerce.payment.application

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.order.application.OrderPaymentService
import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.payment.application.request.PaymentProcessRequest
import com.fastcampus.commerce.payment.domain.entity.Payment
import com.fastcampus.commerce.payment.domain.entity.PaymentMethod
import com.fastcampus.commerce.payment.domain.entity.PaymentStatus
import com.fastcampus.commerce.payment.domain.error.PaymentErrorCode
import com.fastcampus.commerce.payment.domain.model.PgPaymentInfo
import com.fastcampus.commerce.payment.domain.service.PaymentReader
import com.fastcampus.commerce.payment.domain.service.PgClient
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class PaymentServiceTest : FunSpec(
    {
        val timeProvider = mockk<TimeProvider>()
        val pgClient = mockk<PgClient>()
        val orderPaymentService = mockk<OrderPaymentService>()
        val paymentReader = mockk<PaymentReader>()
        val paymentValidator = mockk<PaymentValidator>()

        val paymentService = PaymentService(
            timeProvider = timeProvider,
            pgClient = pgClient,
            orderPaymentService = orderPaymentService,
            paymentReader = paymentReader,
            paymentValidator = paymentValidator,
        )

        beforeTest {
            clearMocks(timeProvider, pgClient, orderPaymentService, paymentReader, paymentValidator)
        }

        context("processPayment") {
            test("정상적으로 결제를 처리하고 결제 번호를 반환한다") {
                // given
                val pgTransactionId = "TOSS-TX-12345"
                val orderNumber = "ORD-20241214-001"
                val paymentNumber = "PAY-20241214-001"
                val amount = 50000
                val now = LocalDateTime.of(2024, 12, 14, 10, 0, 0)

                val request = PaymentProcessRequest(
                    orderNumber = orderNumber,
                    transactionId = pgTransactionId,
                )

                val pgPaymentInfo = PgPaymentInfo(
                    amount = amount,
                    status = "OK",
                )

                val order = Order(
                    orderNumber = orderNumber,
                    userId = 100L,
                    totalAmount = amount,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.WAITING_FOR_PAYMENT,
                ).apply {
                    id = 1L
                }

                val payment = Payment(
                    paymentNumber = paymentNumber,
                    orderId = 1L,
                    userId = 100L,
                    amount = amount,
                    paymentMethod = PaymentMethod.TOSS_PAY,
                    status = PaymentStatus.WAITING,
                ).apply {
                    id = 1L
                    transactionId = pgTransactionId
                }

                every { pgClient.getPaymentInfo(pgTransactionId) } returns pgPaymentInfo
                every { orderPaymentService.getOrder(orderNumber) } returns order
                every { paymentReader.getByOrderId(1L) } returns payment
                every { paymentValidator.validateProcessPayment(pgPaymentInfo, order, payment) } just Runs
                every { timeProvider.now() } returns now

                // when
                val result = paymentService.processPayment(request)

                // then
                result.paymentNumber shouldBe paymentNumber
                payment.status shouldBe PaymentStatus.COMPLETED
                order.status shouldBe OrderStatus.PAID
                order.paidAt shouldBe now

                verify(exactly = 1) { pgClient.getPaymentInfo(pgTransactionId) }
                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 1) { paymentReader.getByOrderId(1L) }
                verify(exactly = 1) { paymentValidator.validateProcessPayment(pgPaymentInfo, order, payment) }
                verify(exactly = 1) { timeProvider.now() }
            }

            test("PG 결제 정보가 없으면 PG_RESULT_NOT_FOUND 예외가 발생한다") {
                // given
                val transactionId = "INVALID-TX-12345"
                val orderNumber = "ORD-20241214-001"

                val request = PaymentProcessRequest(
                    orderNumber = orderNumber,
                    transactionId = transactionId,
                )

                every { pgClient.getPaymentInfo(transactionId) } returns null

                // when & then
                val exception = shouldThrow<CoreException> {
                    paymentService.processPayment(request)
                }

                exception.errorCode shouldBe PaymentErrorCode.PG_RESULT_NOT_FOUND

                verify(exactly = 1) { pgClient.getPaymentInfo(transactionId) }
                verify(exactly = 0) { orderPaymentService.getOrder(any()) }
                verify(exactly = 0) { paymentReader.getByOrderId(any()) }
                verify(exactly = 0) { paymentValidator.validateProcessPayment(any(), any(), any()) }
            }

            test("이미 결제된 주문인 경우 validation에서 예외가 발생한다") {
                // given
                val pgTransactionId = "TOSS-TX-12345"
                val orderNumber = "ORD-20241214-001"
                val amount = 50000

                val request = PaymentProcessRequest(
                    orderNumber = orderNumber,
                    transactionId = pgTransactionId,
                )

                val pgPaymentInfo = PgPaymentInfo(
                    amount = amount,
                    status = "OK",
                )

                val order = Order(
                    orderNumber = orderNumber,
                    userId = 100L,
                    totalAmount = amount,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.PAID, // 이미 결제됨
                ).apply {
                    id = 1L
                }

                val payment = Payment(
                    paymentNumber = "PAY-20241214-001",
                    orderId = 1L,
                    userId = 100L,
                    amount = amount,
                    paymentMethod = PaymentMethod.TOSS_PAY,
                    status = PaymentStatus.COMPLETED, // 이미 결제됨
                ).apply {
                    id = 1L
                    transactionId = pgTransactionId
                }

                every { pgClient.getPaymentInfo(pgTransactionId) } returns pgPaymentInfo
                every { orderPaymentService.getOrder(orderNumber) } returns order
                every { paymentReader.getByOrderId(1L) } returns payment
                every {
                    paymentValidator.validateProcessPayment(pgPaymentInfo, order, payment)
                } throws CoreException(PaymentErrorCode.ALREADY_PAID_ORDER)

                // when & then
                val exception = shouldThrow<CoreException> {
                    paymentService.processPayment(request)
                }

                exception.errorCode shouldBe PaymentErrorCode.ALREADY_PAID_ORDER

                verify(exactly = 1) { pgClient.getPaymentInfo(pgTransactionId) }
                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 1) { paymentReader.getByOrderId(1L) }
                verify(exactly = 1) { paymentValidator.validateProcessPayment(pgPaymentInfo, order, payment) }
                verify(exactly = 0) { timeProvider.now() }
            }

            test("PG 결제 금액과 주문 금액이 다른 경우 validation에서 예외가 발생한다") {
                // given
                val pgTransactionId = "TOSS-TX-12345"
                val orderNumber = "ORD-20241214-001"

                val request = PaymentProcessRequest(
                    orderNumber = orderNumber,
                    transactionId = pgTransactionId,
                )

                val pgPaymentInfo = PgPaymentInfo(
                    amount = 60000, // PG에서 받은 금액
                    status = "OK",
                )

                val order = Order(
                    orderNumber = orderNumber,
                    userId = 100L,
                    totalAmount = 50000, // 실제 주문 금액
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.WAITING_FOR_PAYMENT,
                ).apply {
                    id = 1L
                }

                val payment = Payment(
                    paymentNumber = "PAY-20241214-001",
                    orderId = 1L,
                    userId = 100L,
                    amount = 50000, // 결제 예정 금액
                    paymentMethod = PaymentMethod.TOSS_PAY,
                    status = PaymentStatus.WAITING,
                ).apply {
                    id = 1L
                    transactionId = pgTransactionId
                }

                every { pgClient.getPaymentInfo(pgTransactionId) } returns pgPaymentInfo
                every { orderPaymentService.getOrder(orderNumber) } returns order
                every { paymentReader.getByOrderId(1L) } returns payment
                every {
                    paymentValidator.validateProcessPayment(pgPaymentInfo, order, payment)
                } throws CoreException(PaymentErrorCode.PG_RESULT_NOT_MATCH_PAYMENT)

                // when & then
                val exception = shouldThrow<CoreException> {
                    paymentService.processPayment(request)
                }

                exception.errorCode shouldBe PaymentErrorCode.PG_RESULT_NOT_MATCH_PAYMENT

                verify(exactly = 1) { pgClient.getPaymentInfo(pgTransactionId) }
                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 1) { paymentReader.getByOrderId(1L) }
                verify(exactly = 1) { paymentValidator.validateProcessPayment(pgPaymentInfo, order, payment) }
                verify(exactly = 0) { timeProvider.now() }
            }
        }

        context("cancelPayment") {
            test("정상적으로 결제를 취소한다") {
                // given
                val userId = 100L
                val orderNumber = "ORD-20241214-001"
                val now = LocalDateTime.of(2024, 12, 14, 15, 0, 0)

                val order = Order(
                    orderNumber = orderNumber,
                    userId = userId,
                    totalAmount = 50000,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.PAID,
                ).apply {
                    id = 1L
                }

                val payment = Payment(
                    paymentNumber = "PAY-20241214-001",
                    orderId = 1L,
                    userId = userId,
                    amount = 50000,
                    paymentMethod = PaymentMethod.TOSS_PAY,
                    status = PaymentStatus.COMPLETED,
                ).apply {
                    id = 1L
                    transactionId = "TRANSACTION_ID"
                }

                every { orderPaymentService.getOrder(orderNumber) } returns order
                every { paymentReader.getByOrderId(1L) } returns payment
                every { timeProvider.now() } returns now
                every { pgClient.refund(any(), any()) } just Runs

                // when
                paymentService.cancelPayment(userId, orderNumber)

                // then
                payment.status shouldBe PaymentStatus.CANCELLED
                order.status shouldBe OrderStatus.CANCELLED
                order.canceledAt shouldBe now

                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 1) { paymentReader.getByOrderId(1L) }
                verify(exactly = 1) { timeProvider.now() }
            }

            test("다른 사용자가 주문을 취소하려고 하면 예외가 발생한다") {
                // given
                val orderUserId = 100L
                val cancelUserId = 200L
                val orderNumber = "ORD-20241214-001"

                val order = Order(
                    orderNumber = orderNumber,
                    userId = orderUserId,
                    totalAmount = 50000,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.PAID,
                ).apply {
                    id = 1L
                }

                every { orderPaymentService.getOrder(orderNumber) } returns order

                // when & then
                val exception = shouldThrow<CoreException> {
                    paymentService.cancelPayment(cancelUserId, orderNumber)
                }

                exception.errorCode shouldBe PaymentErrorCode.UNAUTHORIZED_ORDER_CANCEL

                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 0) { paymentReader.getByOrderId(any()) }
                verify(exactly = 0) { timeProvider.now() }
            }

            test("취소 불가능한 상태의 주문을 취소하려고 하면 예외가 발생한다") {
                // given
                val userId = 100L
                val orderNumber = "ORD-20241214-001"

                val order = Order(
                    orderNumber = orderNumber,
                    userId = userId,
                    totalAmount = 50000,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.SHIPPED, // 배송 중인 상태
                ).apply {
                    id = 1L
                }

                every { orderPaymentService.getOrder(orderNumber) } returns order

                // when & then
                val exception = shouldThrow<CoreException> {
                    paymentService.cancelPayment(userId, orderNumber)
                }

                exception.errorCode shouldBe PaymentErrorCode.CANNOT_CANCEL

                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 0) { paymentReader.getByOrderId(any()) }
                verify(exactly = 0) { timeProvider.now() }
            }

            test("결제 대기 중인 주문을 취소할 수 있다") {
                // given
                val userId = 100L
                val orderNumber = "ORD-20241214-001"
                val now = LocalDateTime.of(2024, 12, 14, 15, 0, 0)

                val order = Order(
                    orderNumber = orderNumber,
                    userId = userId,
                    totalAmount = 50000,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.WAITING_FOR_PAYMENT,
                ).apply {
                    id = 1L
                }

                val payment = Payment(
                    paymentNumber = "PAY-20241214-001",
                    orderId = 1L,
                    userId = userId,
                    amount = 50000,
                    paymentMethod = PaymentMethod.TOSS_PAY,
                    status = PaymentStatus.WAITING,
                ).apply {
                    id = 1L
                    transactionId = "TRANSACTION_ID"
                }

                every { orderPaymentService.getOrder(orderNumber) } returns order
                every { paymentReader.getByOrderId(1L) } returns payment
                every { timeProvider.now() } returns now
                every { pgClient.refund(any(), any()) } just Runs

                // when
                paymentService.cancelPayment(userId, orderNumber)

                // then
                payment.status shouldBe PaymentStatus.CANCELLED
                order.status shouldBe OrderStatus.CANCELLED
                order.canceledAt shouldBe now

                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 1) { paymentReader.getByOrderId(1L) }
                verify(exactly = 1) { timeProvider.now() }
            }
        }

        context("refundRequestPayment") {
            test("배송 완료된 주문을 정상적으로 환불 요청한다") {
                // given
                val userId = 100L
                val orderNumber = "ORD-20241214-001"
                val now = LocalDateTime.of(2024, 12, 14, 17, 0, 0)

                val order = Order(
                    orderNumber = orderNumber,
                    userId = userId,
                    totalAmount = 50000,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.DELIVERED,
                ).apply {
                    id = 1L
                }

                val payment = Payment(
                    paymentNumber = "PAY-20241214-001",
                    orderId = 1L,
                    userId = userId,
                    amount = 50000,
                    paymentMethod = PaymentMethod.TOSS_PAY,
                    status = PaymentStatus.COMPLETED,
                ).apply {
                    id = 1L
                    transactionId = "TRANSACTION_ID"
                }

                every { orderPaymentService.getOrder(orderNumber) } returns order
                every { timeProvider.now() } returns now

                // when
                paymentService.refundRequestPayment(userId, orderNumber)

                // then
                order.status shouldBe OrderStatus.REFUND_REQUESTED
                order.refundRequestedAt shouldBe now

                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 1) { timeProvider.now() }
            }

            test("배송 중인 주문을 정상적으로 환불요청한다") {
                // given
                val userId = 100L
                val orderNumber = "ORD-20241214-001"
                val now = LocalDateTime.of(2024, 12, 14, 17, 0, 0)

                val order = Order(
                    orderNumber = orderNumber,
                    userId = userId,
                    totalAmount = 30000,
                    recipientName = "김철수",
                    recipientPhone = "010-9876-5432",
                    zipCode = "54321",
                    address1 = "서울시 서초구",
                    status = OrderStatus.SHIPPED,
                ).apply {
                    id = 2L
                }

                val payment = Payment(
                    paymentNumber = "PAY-20241214-002",
                    orderId = 2L,
                    userId = userId,
                    amount = 30000,
                    paymentMethod = PaymentMethod.MOCK,
                    status = PaymentStatus.COMPLETED,
                ).apply {
                    id = 2L
                    transactionId = "KAKAO_TX_12345"
                }

                every { orderPaymentService.getOrder(orderNumber) } returns order
                every { timeProvider.now() } returns now

                // when
                paymentService.refundRequestPayment(userId, orderNumber)

                // then
                order.status shouldBe OrderStatus.REFUND_REQUESTED
                order.refundRequestedAt shouldBe now

                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 1) { timeProvider.now() }
            }

            test("다른 사용자가 환불을 요청하면 예외가 발생한다") {
                // given
                val orderUserId = 100L
                val refundUserId = 200L
                val orderNumber = "ORD-20241214-001"
                val now = LocalDateTime.of(2024, 12, 14, 17, 0, 0)

                val order = Order(
                    orderNumber = orderNumber,
                    userId = orderUserId,
                    totalAmount = 50000,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.DELIVERED,
                ).apply {
                    id = 1L
                }

                every { orderPaymentService.getOrder(orderNumber) } returns order
                every { timeProvider.now() } returns now

                shouldThrow<CoreException> {
                    paymentService.refundRequestPayment(refundUserId, orderNumber)
                }.errorCode shouldBe PaymentErrorCode.UNAUTHORIZED_ORDER_REFUND

                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 1) { timeProvider.now() }
            }

            test("환불 불가능한 상태의 주문에 대해 환불을 요청하면 예외가 발생한다") {
                // given
                val userId = 100L
                val orderNumber = "ORD-20241214-001"
                val now = LocalDateTime.of(2024, 12, 14, 17, 0, 0)

                val order = Order(
                    orderNumber = orderNumber,
                    userId = userId,
                    totalAmount = 50000,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.PAID, // 아직 배송 전
                ).apply {
                    id = 1L
                }

                every { orderPaymentService.getOrder(orderNumber) } returns order
                every { timeProvider.now() } returns now

                shouldThrow<CoreException> {
                    paymentService.refundRequestPayment(userId, orderNumber)
                }.errorCode shouldBe PaymentErrorCode.CANNOT_REFUND

                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 1) { timeProvider.now() }
            }

            test("이미 취소된 주문에 대해 환불을 요청하면 예외가 발생한다") {
                // given
                val userId = 100L
                val orderNumber = "ORD-20241214-001"
                val now = LocalDateTime.of(2024, 12, 14, 17, 0, 0)

                val order = Order(
                    orderNumber = orderNumber,
                    userId = userId,
                    totalAmount = 50000,
                    recipientName = "홍길동",
                    recipientPhone = "010-1234-5678",
                    zipCode = "12345",
                    address1 = "서울시 강남구",
                    status = OrderStatus.CANCELLED, // 이미 취소됨
                ).apply {
                    id = 1L
                }

                every { orderPaymentService.getOrder(orderNumber) } returns order
                every { timeProvider.now() } returns now

                shouldThrow<CoreException> {
                    paymentService.refundRequestPayment(userId, orderNumber)
                }.errorCode shouldBe PaymentErrorCode.CANNOT_REFUND

                verify(exactly = 1) { orderPaymentService.getOrder(orderNumber) }
                verify(exactly = 1) { timeProvider.now() }
            }
        }
    },
)
