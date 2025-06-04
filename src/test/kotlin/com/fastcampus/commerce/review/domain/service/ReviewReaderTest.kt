package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.entity.Review
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.domain.repository.ReviewRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class ReviewReaderTest : FunSpec(
    {
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

        context("getReviewById") {
            val reviewId = 1L

            test("리뷰 아이디로 리뷰를 조회할 수 있다.") {
                val review = Review(
                    userId = 1L,
                    orderItemId = 100L,
                    productId = 10L,
                    rating = 5,
                    content = "좋습니다.",
                )
                every { reviewRepository.findById(reviewId) } returns Optional.of(review)

                val result = reviewReader.getReviewById(reviewId)

                result shouldBe review
            }

            test("리뷰 아이디와 일치하는 리뷰가 없으면 REVIEW_NOT_FOUND 예외가 발생한다.") {
                every { reviewRepository.findById(reviewId) } returns Optional.empty()

                shouldThrow<CoreException> {
                    reviewReader.getReviewById(reviewId)
                }.errorCode shouldBe ReviewErrorCode.REVIEW_NOT_FOUND
            }
        }
    },
)
