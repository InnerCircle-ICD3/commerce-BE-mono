package com.fastcampus.commerce.order.application.review

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.order.domain.repository.OrderReviewRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

class OrderReviewServiceTest : FunSpec({

    val orderReviewRepository = mockk<OrderReviewRepository>()
    val orderReviewService = OrderReviewService(orderReviewRepository)

    val orderNumber = "ORDER1234"
    val orderItemId = 100L

    test("주문번호와 주문상품ID로 리뷰 대상 정보를 조회할 수 있다.") {
        val orderReview = OrderReview(
            deliveredAt = LocalDateTime.of(2025, 6, 4, 12, 0),
            productId = 1L,
        )
        every { orderReviewRepository.findOrderReview(orderNumber, orderItemId) } returns orderReview

        val result = orderReviewService.getReviewInfo(orderNumber, orderItemId)

        result shouldBe orderReview
    }

    test("주문번호/주문상품ID에 해당하는 리뷰 대상 정보가 없으면 ORDER_DATA_FOR_REVIEW_NOT_FOUND 예외를 던진다.") {
        every { orderReviewRepository.findOrderReview(orderNumber, orderItemId) } returns null

        shouldThrow<CoreException> {
            orderReviewService.getReviewInfo(orderNumber, orderItemId)
        }.errorCode shouldBe OrderErrorCode.ORDER_DATA_FOR_REVIEW_NOT_FOUND
    }
})
