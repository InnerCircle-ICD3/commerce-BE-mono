package com.fastcampus.commerce.review.application

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.order.application.review.OrderReview
import com.fastcampus.commerce.order.application.review.OrderReviewService
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.review.application.request.RegisterReviewRequest
import com.fastcampus.commerce.review.application.request.UpdateReviewRequest
import com.fastcampus.commerce.review.domain.entity.Review
import com.fastcampus.commerce.review.domain.service.ReviewStore
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class ReviewCommandServiceTest : FunSpec({

    val orderReviewService = mockk<OrderReviewService>()
    val reviewStore = mockk<ReviewStore>()
    val reviewCommandService = ReviewCommandService(orderReviewService, reviewStore)

    val userId = 1L

    context("registerReview") {
        val request = RegisterReviewRequest(
            orderNumber = "ORDER1234",
            orderItemId = 100L,
            rating = 5,
            content = "아주 좋아요!",
        )

        test("리뷰 등록 요청 시 리뷰가 생성되고 id를 반환한다") {
            val orderReview = OrderReview(
                deliveredAt = LocalDateTime.of(2025, 6, 1, 12, 0),
                productId = 10L,
            )
            val command = request.toCommand(userId, orderReview)
            val reviewId = 1L
            val review = Review(
                userId = command.userId,
                orderItemId = command.orderItemId,
                productId = command.productId,
                rating = command.rating,
                content = command.content,
            ).apply { id = reviewId }

            every {
                orderReviewService.getReviewInfo(request.orderNumber, request.orderItemId)
            } returns orderReview
            every { reviewStore.register(command) } returns review

            val result = reviewCommandService.registerReview(userId, request)

            result shouldBe reviewId
        }

        test("리뷰 등록 요청 시 주문 정보가 없으면 ORDER_DATA_FOR_REVIEW_NOT_FOUND 예외가 발생한다.") {
            every {
                orderReviewService.getReviewInfo(request.orderNumber, request.orderItemId)
            } throws CoreException(OrderErrorCode.ORDER_DATA_FOR_REVIEW_NOT_FOUND)

            shouldThrow<CoreException> {
                reviewCommandService.registerReview(userId, request)
            }.errorCode shouldBe OrderErrorCode.ORDER_DATA_FOR_REVIEW_NOT_FOUND
        }
    }

    context("updateReview") {
        val reviewId = 1L
        val updateRequest = UpdateReviewRequest(
            rating = 4,
            content = "별로였어요.",
        )
        val command = updateRequest.toCommand(userId, reviewId)

        test("리뷰 수정 요청 시 ReviewStore.update가 호출되고 reviewId를 반환한다") {
            every { reviewStore.update(command) } just Runs

            val result = reviewCommandService.updateReview(userId, reviewId, updateRequest)

            result shouldBe reviewId

            verify(exactly = 1) { reviewStore.update(command) }
        }
    }
    context("deleteReview") {
        val userId = 1L
        val reviewId = 1L

        test("리뷰 삭제 요청 시 ReviewStore.delete가 호출된다") {
            every { reviewStore.delete(userId, reviewId) } just Runs

            reviewCommandService.deleteReview(userId, reviewId)

            verify(exactly = 1) { reviewStore.delete(userId, reviewId) }
        }
    }
})
