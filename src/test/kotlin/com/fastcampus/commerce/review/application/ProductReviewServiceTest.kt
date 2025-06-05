package com.fastcampus.commerce.review.application

import com.fastcampus.commerce.review.application.response.ProductReviewResponse
import com.fastcampus.commerce.review.domain.model.AdminReply
import com.fastcampus.commerce.review.domain.model.ProductReview
import com.fastcampus.commerce.review.domain.repository.ProductReviewRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

class ProductReviewServiceTest : DescribeSpec({

    val productReviewRepository = mockk<ProductReviewRepository>()
    val productReviewService = ProductReviewService(productReviewRepository)

    beforeTest {
        clearAllMocks()
    }

    describe("상품 리뷰 목록 조회") {
        it("상품에 등록된 리뷰 목록을 조회할 수 있다.") {
            val productId = 1L
            val pageable = PageRequest.of(1, 10)

            val now = LocalDateTime.of(2025, 6, 1, 12, 0)
            val productReviews = listOf(
                ProductReview(
                    reviewId = 1L,
                    rating = 5,
                    content = "아주 만족해요",
                    createdAt = now,
                    adminReply = AdminReply(
                        content = "감사합니다",
                        createdAt = now,
                    ),
                ),
                ProductReview(
                    reviewId = 2L,
                    rating = 3,
                    content = "좋습니다.",
                    createdAt = now,
                ),
            )
            val reviews = PageImpl(productReviews, pageable, productReviews.size.toLong())
            every { productReviewRepository.getProductReviews(productId, pageable) } returns reviews

            val result = productReviewService.getProductReviews(productId, pageable)

            result.content shouldHaveSize productReviews.size
            result.content[0] shouldBe ProductReviewResponse.from(productReviews[0])
            result.content[1] shouldBe ProductReviewResponse.from(productReviews[1])
        }
    }
})
