package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.review.domain.repository.ReviewRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ReviewReaderTest : FunSpec({
    val reviewRepository = mockk<ReviewRepository>()
    val reviewReader = ReviewReader(reviewRepository)

    context("existsByUserIdAndOrderItemId") {
        val userId = 1L
        val orderItemId = 100L

        test("userId와 orderItemId 조합으로 작성된 리뷰가 있으면 true를 반환한다.") {
            every { reviewRepository.existsByUserIdAndOrderItemId(userId, orderItemId) } returns true

            val result = reviewReader.existsByUserIdAndOrderItemId(userId, orderItemId)

            result shouldBe true
        }

        test("userId와 orderItemId 조합으로 작성된 리뷰가 없으면 false를 반환한다.") {
            every { reviewRepository.existsByUserIdAndOrderItemId(userId, orderItemId) } returns false

            val result = reviewReader.existsByUserIdAndOrderItemId(userId, orderItemId)

            result shouldBe false
        }
    }
})
