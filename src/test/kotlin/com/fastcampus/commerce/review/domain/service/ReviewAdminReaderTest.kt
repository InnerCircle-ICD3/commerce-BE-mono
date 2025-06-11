package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.review.domain.model.AdminReply
import com.fastcampus.commerce.review.domain.model.ReviewAdminInfoFlat
import com.fastcampus.commerce.review.domain.model.ReviewAuthor
import com.fastcampus.commerce.review.domain.model.ReviewProduct
import com.fastcampus.commerce.review.domain.model.SearchReviewAdminCondition
import com.fastcampus.commerce.review.domain.repository.ReviewAdminRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.LocalDateTime

class ReviewAdminReaderTest : FunSpec({

    val reviewAdminRepository = mockk<ReviewAdminRepository>()
    val reviewAdminReader = ReviewAdminReader(reviewAdminRepository)

    context("searchReviews") {
        val condition = SearchReviewAdminCondition(
            productId = 1L,
            productName = "테스트 상품",
            rating = 5,
            content = "좋아요",
            from = LocalDate.of(2024, 1, 1),
            to = LocalDate.of(2024, 12, 31),
        )
        val pageable = PageRequest.of(0, 10)

        test("관리자 답글이 있는 리뷰를 올바르게 변환한다") {
            val adminReplyCreatedAt = LocalDateTime.of(2024, 6, 15, 10, 30)
            val reviewCreatedAt = LocalDateTime.of(2024, 6, 10, 14, 20)

            val flatReview = ReviewAdminInfoFlat(
                reviewId = 1L,
                rating = 5,
                content = "정말 좋은 상품입니다",
                adminReplyContent = "감사합니다",
                adminReplyCreatedAt = adminReplyCreatedAt,
                userId = 100L,
                userNickname = "사용자1",
                productId = 1L,
                productName = "테스트 상품",
                createdAt = reviewCreatedAt,
            )

            val flatReviews = listOf(flatReview)
            val page = PageImpl(flatReviews, pageable, 1)

            every { reviewAdminRepository.searchReviews(condition, pageable) } returns page

            val result = reviewAdminReader.searchReviews(condition, pageable)

            result.totalElements shouldBe 1
            result.content.size shouldBe 1

            val reviewInfo = result.content[0]
            reviewInfo.reviewId shouldBe 1L
            reviewInfo.rating shouldBe 5
            reviewInfo.content shouldBe "정말 좋은 상품입니다"
            reviewInfo.adminReply shouldBe AdminReply("감사합니다", adminReplyCreatedAt)
            reviewInfo.user shouldBe ReviewAuthor(100L, "사용자1")
            reviewInfo.product shouldBe ReviewProduct(1L, "테스트 상품")
            reviewInfo.createdAt shouldBe reviewCreatedAt
        }

        test("관리자 답글이 없는 리뷰를 올바르게 변환한다") {
            val reviewCreatedAt = LocalDateTime.of(2024, 6, 10, 14, 20)

            val flatReview = ReviewAdminInfoFlat(
                reviewId = 2L,
                rating = 3,
                content = "보통입니다",
                adminReplyContent = null,
                adminReplyCreatedAt = null,
                userId = 200L,
                userNickname = "사용자2",
                productId = 2L,
                productName = "다른 상품",
                createdAt = reviewCreatedAt,
            )

            val flatReviews = listOf(flatReview)
            val page = PageImpl(flatReviews, pageable, 1)

            every { reviewAdminRepository.searchReviews(condition, pageable) } returns page

            val result = reviewAdminReader.searchReviews(condition, pageable)

            result.totalElements shouldBe 1
            result.content.size shouldBe 1

            val reviewInfo = result.content[0]
            reviewInfo.reviewId shouldBe 2L
            reviewInfo.rating shouldBe 3
            reviewInfo.content shouldBe "보통입니다"
            reviewInfo.adminReply shouldBe null
            reviewInfo.user shouldBe ReviewAuthor(200L, "사용자2")
            reviewInfo.product shouldBe ReviewProduct(2L, "다른 상품")
            reviewInfo.createdAt shouldBe reviewCreatedAt
        }

        test("관리자 답글 내용만 있고 생성일시가 없는 경우 답글을 null로 처리한다") {
            val reviewCreatedAt = LocalDateTime.of(2024, 6, 10, 14, 20)

            val flatReview = ReviewAdminInfoFlat(
                reviewId = 3L,
                rating = 4,
                content = "괜찮습니다",
                adminReplyContent = "답변입니다",
                adminReplyCreatedAt = null,
                userId = 300L,
                userNickname = "사용자3",
                productId = 3L,
                productName = "상품3",
                createdAt = reviewCreatedAt,
            )

            val flatReviews = listOf(flatReview)
            val page = PageImpl(flatReviews, pageable, 1)

            every { reviewAdminRepository.searchReviews(condition, pageable) } returns page

            val result = reviewAdminReader.searchReviews(condition, pageable)

            val reviewInfo = result.content[0]
            reviewInfo.adminReply shouldBe null
        }

        test("관리자 답글 생성일시만 있고 내용이 없는 경우 답글을 null로 처리한다") {
            val reviewCreatedAt = LocalDateTime.of(2024, 6, 10, 14, 20)
            val adminReplyCreatedAt = LocalDateTime.of(2024, 6, 15, 10, 30)

            val flatReview = ReviewAdminInfoFlat(
                reviewId = 4L,
                rating = 2,
                content = "별로입니다",
                adminReplyContent = null,
                adminReplyCreatedAt = adminReplyCreatedAt,
                userId = 400L,
                userNickname = "사용자4",
                productId = 4L,
                productName = "상품4",
                createdAt = reviewCreatedAt,
            )

            val flatReviews = listOf(flatReview)
            val page = PageImpl(flatReviews, pageable, 1)

            every { reviewAdminRepository.searchReviews(condition, pageable) } returns page

            val result = reviewAdminReader.searchReviews(condition, pageable)

            val reviewInfo = result.content[0]
            reviewInfo.adminReply shouldBe null
        }

        test("여러 리뷰를 한 번에 변환할 수 있다") {
            val reviewCreatedAt1 = LocalDateTime.of(2024, 6, 10, 14, 20)
            val reviewCreatedAt2 = LocalDateTime.of(2024, 6, 11, 15, 30)
            val adminReplyCreatedAt = LocalDateTime.of(2024, 6, 15, 10, 30)

            val flatReview1 = ReviewAdminInfoFlat(
                reviewId = 1L,
                rating = 5,
                content = "좋습니다",
                adminReplyContent = "감사합니다",
                adminReplyCreatedAt = adminReplyCreatedAt,
                userId = 100L,
                userNickname = "사용자1",
                productId = 1L,
                productName = "상품1",
                createdAt = reviewCreatedAt1,
            )

            val flatReview2 = ReviewAdminInfoFlat(
                reviewId = 2L,
                rating = 3,
                content = "보통입니다",
                adminReplyContent = null,
                adminReplyCreatedAt = null,
                userId = 200L,
                userNickname = "사용자2",
                productId = 2L,
                productName = "상품2",
                createdAt = reviewCreatedAt2,
            )

            val flatReviews = listOf(flatReview1, flatReview2)
            val page = PageImpl(flatReviews, pageable, 2)

            every { reviewAdminRepository.searchReviews(condition, pageable) } returns page

            val result = reviewAdminReader.searchReviews(condition, pageable)

            result.totalElements shouldBe 2
            result.content.size shouldBe 2

            // 첫 번째 리뷰 (관리자 답글 있음)
            val reviewInfo1 = result.content[0]
            reviewInfo1.reviewId shouldBe 1L
            reviewInfo1.adminReply shouldBe AdminReply("감사합니다", adminReplyCreatedAt)

            // 두 번째 리뷰 (관리자 답글 없음)
            val reviewInfo2 = result.content[1]
            reviewInfo2.reviewId shouldBe 2L
            reviewInfo2.adminReply shouldBe null
        }

        test("빈 결과를 올바르게 처리한다") {
            val emptyPage = PageImpl<ReviewAdminInfoFlat>(emptyList(), pageable, 0)

            every { reviewAdminRepository.searchReviews(condition, pageable) } returns emptyPage

            val result = reviewAdminReader.searchReviews(condition, pageable)

            result.totalElements shouldBe 0
            result.content.size shouldBe 0
            result.isEmpty shouldBe true
        }
    }
})
