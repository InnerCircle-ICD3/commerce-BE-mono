package com.fastcampus.commerce.order.application

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.order.domain.repository.OrderPaymentRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class OrderPaymentServiceTest : FunSpec({
    val orderPaymentRepository = mockk<OrderPaymentRepository>()
    val orderPaymentService = OrderPaymentService(orderPaymentRepository)

    context("getOrder 메서드는") {
        test("주문번호로 주문을 조회한다") {
            // Given
            val orderNumber = "ORD20240101000001"
            val expectedOrder = Order(
                orderNumber = orderNumber,
                userId = 1L,
                totalAmount = 50000,
                recipientName = "테스트유저",
                recipientPhone = "010-1111-2222",
                zipCode = "06234",
                address1 = "서울시 강남구 테헤란로",
                address2 = "123동 456호",
                deliveryMessage = "문 앞에 놓아주세요",
                status = OrderStatus.WAITING_FOR_PAYMENT,
            )

            every { orderPaymentRepository.findOrderByOrderNumber(orderNumber) } returns Optional.of(expectedOrder)

            // When
            val result = orderPaymentService.getOrder(orderNumber)

            // Then
            result shouldBe expectedOrder
            verify(exactly = 1) { orderPaymentRepository.findOrderByOrderNumber(orderNumber) }
        }

        test("존재하지 않는 주문번호로 조회시 ORDER_NOT_FOUND 예외를 던진다") {
            // Given
            val nonExistentOrderNumber = "ORD99999999999999"

            every { orderPaymentRepository.findOrderByOrderNumber(nonExistentOrderNumber) } returns Optional.empty()

            // When & Then
            val exception = shouldThrow<CoreException> {
                orderPaymentService.getOrder(nonExistentOrderNumber)
            }

            exception.errorCode shouldBe OrderErrorCode.ORDER_NOT_FOUND
            verify(exactly = 1) { orderPaymentRepository.findOrderByOrderNumber(nonExistentOrderNumber) }
        }
    }
})
