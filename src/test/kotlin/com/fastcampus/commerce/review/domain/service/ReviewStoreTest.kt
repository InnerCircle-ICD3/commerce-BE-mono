package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.util.FixedTimeProvider
import com.fastcampus.commerce.review.domain.entity.Review
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.domain.model.ReviewRegister
import com.fastcampus.commerce.review.domain.repository.ReviewRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ReviewStoreTest : FunSpec({
    val timeProvider = FixedTimeProvider()
    val reviewReader = mockk<ReviewReader>()
    val reviewRepository = mockk<ReviewRepository>()
    val reviewStore = ReviewStore(
        timeProvider = timeProvider,
        reviewReader = reviewReader,
        reviewRepository = reviewRepository,
    )

    beforeEach {
        clearAllMocks()
    }

    context("register") {
        val register = ReviewRegister(
            userId = 1L,
            orderItemId = 100L,
            productId = 10L,
            rating = 5,
            content = "좋아요",
        )
        test("리뷰가 정상적으로 등록된다") {
            val deliveredAt = timeProvider.now().minusDays(Review.MAX_WRITTEN_DATE)
            val validRegister = register.copy(deliveredAt = deliveredAt)
            val review = validRegister.toReview()
            every {
                reviewReader.existsByUserIdAndOrderItemId(validRegister.userId, validRegister.orderItemId)
            } returns false
            every { reviewRepository.save(any<Review>()) } returns review

            val result = reviewStore.register(validRegister)

            result shouldBe review

            verify(exactly = 1) { reviewRepository.save(any<Review>()) }
        }

        test("배송 시간이 null인 경우 ORDER_NOT_DELIVERED 예외가 발생한다.") {
            val invalidRegister = register.copy(deliveredAt = null)

            shouldThrow<CoreException> {
                reviewStore.register(invalidRegister)
            }.errorCode shouldBe ReviewErrorCode.ORDER_NOT_DELIVERED

            verify(exactly = 0) { reviewRepository.save(any()) }
        }

        test("리뷰 작성 기한이 지나면 TOO_LATE 예외가 발생한다.") {
            val deliveredAt = timeProvider.now().minusDays(Review.MAX_WRITTEN_DATE + 1)
            val expiredRegister = register.copy(deliveredAt = deliveredAt)

            shouldThrow<CoreException> {
                reviewStore.register(expiredRegister)
            }.errorCode shouldBe ReviewErrorCode.TOO_LATE

            verify(exactly = 0) { reviewRepository.save(any()) }
        }

        test("이미 작성된 리뷰가 있으면 ALREADY_WRITE 예외가 발생한다.") {
            val deliveredAt = timeProvider.now()
            val register = register.copy(deliveredAt = deliveredAt)
            every { reviewReader.existsByUserIdAndOrderItemId(register.userId, register.orderItemId) } returns true

            shouldThrow<CoreException> {
                reviewStore.register(register)
            }.errorCode shouldBe ReviewErrorCode.ALREADY_WRITE

            verify(exactly = 0) { reviewRepository.save(any()) }
        }
    }
})
