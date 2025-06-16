package com.fastcampus.commerce.review.application

import com.fastcampus.commerce.review.application.response.ProductReviewResponse
import com.fastcampus.commerce.review.domain.model.ProductReviewFlat
import com.fastcampus.commerce.review.domain.model.ProductReviewRating
import com.fastcampus.commerce.review.domain.repository.ProductReviewRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
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
                ProductReviewFlat(
                    reviewId = 1L,
                    rating = 5,
                    content = "아주 만족해요",
                    createdAt = now,
                    replyContent = "감사합니다",
                    replyCreatedAt = now,
                ),
                ProductReviewFlat(
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

    describe("상품 리뷰 별점 통계") {
        it("상품에 등록된 리뷰의 별점 통계를 조회할 수 있다.") {
            val productId = 1L
            val rating = ProductReviewRating(
                average = 3.5,
                totalCount = 10,
                oneStarCount = 2,
                twoStarsCount = 3,
                threeStarsCount = 5,
                fourStarsCount = 1,
                fiveStarsCount = 8,
            )
            every { productReviewRepository.getProductReviewRating(productId) } returns rating

            val result = productReviewService.getProductReviewRating(productId)

            result.averageRating shouldBe rating.average
            result.totalCount shouldBe rating.totalCount
            result.ratingDistribution.oneStarCount shouldBe rating.oneStarCount
            result.ratingDistribution.twoStarsCount shouldBe rating.twoStarsCount
            result.ratingDistribution.threeStarsCount shouldBe rating.threeStarsCount
            result.ratingDistribution.fourStarsCount shouldBe rating.fourStarsCount
            result.ratingDistribution.fiveStarsCount shouldBe rating.fiveStarsCount
        }
        it("올바르지 않은 상품아이디로 별점 통계를 조회하면 기본 항목이 조회횐다.") {
            val productId = 1L
            every { productReviewRepository.getProductReviewRating(productId) } returns null

            val result = productReviewService.getProductReviewRating(productId)

            result.averageRating shouldBe 0
            result.totalCount shouldBe 0
            result.ratingDistribution.oneStarCount shouldBe 0
            result.ratingDistribution.twoStarsCount shouldBe 0
            result.ratingDistribution.threeStarsCount shouldBe 0
            result.ratingDistribution.fourStarsCount shouldBe 0
            result.ratingDistribution.fiveStarsCount shouldBe 0
        }
    }
})
