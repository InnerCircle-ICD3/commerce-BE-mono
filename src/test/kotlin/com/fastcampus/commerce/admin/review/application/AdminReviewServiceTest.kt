package com.fastcampus.commerce.admin.review.application

import com.fastcampus.commerce.admin.review.application.request.SearchReviewAdminRequest
import com.fastcampus.commerce.admin.review.application.response.SearchReviewAdminAuthorResponse
import com.fastcampus.commerce.admin.review.application.response.SearchReviewAdminProductResponse
import com.fastcampus.commerce.admin.review.application.response.SearchReviewAdminReplyResponse
import com.fastcampus.commerce.admin.review.application.response.SearchReviewAdminResponse
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.review.domain.model.AdminReply
import com.fastcampus.commerce.review.domain.model.ReviewAdminInfo
import com.fastcampus.commerce.review.domain.model.ReviewAuthor
import com.fastcampus.commerce.review.domain.model.ReviewProduct
import com.fastcampus.commerce.review.domain.model.SearchReviewAdminCondition
import com.fastcampus.commerce.review.domain.service.ReviewAdminReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.LocalDateTime

class AdminReviewServiceTest : FunSpec({

    val timeProvider = mockk<TimeProvider>()
    val reviewAdminReader = mockk<ReviewAdminReader>()
    val adminReviewService = AdminReviewService(timeProvider, reviewAdminReader)

    context("search") {
        val now = LocalDateTime.of(2024, 6, 15, 14, 30, 0)
        val pageable = PageRequest.of(0, 10)

        test("검색 요청을 처리하고 응답을 반환한다") {
            val request = SearchReviewAdminRequest(
                productId = 1L,
                productName = "테스트 상품",
                rating = 5,
                content = "좋아요",
                period = 3,
            )

            val expectedCondition = SearchReviewAdminCondition(
                productId = 1L,
                productName = "테스트 상품",
                rating = 5,
                content = "좋아요",
                from = LocalDate.of(2024, 3, 15), // 3개월 전
                to = LocalDate.of(2024, 6, 15),
            )

            val adminReply = AdminReply("감사합니다", LocalDateTime.of(2024, 6, 16, 10, 0))
            val reviewAdminInfo = ReviewAdminInfo(
                reviewId = 1L,
                rating = 5,
                content = "정말 좋은 상품입니다",
                adminReply = adminReply,
                user = ReviewAuthor(100L, "사용자1"),
                product = ReviewProduct(1L, "테스트 상품"),
                createdAt = LocalDateTime.of(2024, 6, 10, 14, 20),
            )

            val reviewPage = PageImpl(listOf(reviewAdminInfo), pageable, 1)

            every { timeProvider.now() } returns now
            every { reviewAdminReader.searchReviews(expectedCondition, pageable) } returns reviewPage

            val result = adminReviewService.search(request, pageable)

            result.totalElements shouldBe 1
            result.content.size shouldBe 1

            val response = result.content[0]
            response shouldBe SearchReviewAdminResponse(
                reviewId = 1L,
                rating = 5,
                content = "정말 좋은 상품입니다",
                adminReply = SearchReviewAdminReplyResponse("감사합니다", LocalDateTime.of(2024, 6, 16, 10, 0)),
                user = SearchReviewAdminAuthorResponse(100L, "사용자1"),
                product = SearchReviewAdminProductResponse(1L, "테스트 상품"),
                createdAt = LocalDateTime.of(2024, 6, 10, 14, 20),
            )

            verify(exactly = 1) { timeProvider.now() }
            verify(exactly = 1) { reviewAdminReader.searchReviews(expectedCondition, pageable) }
        }

        test("여러 리뷰 검색 결과를 올바르게 변환한다") {
            val request = SearchReviewAdminRequest(period = 12)

            val expectedCondition = SearchReviewAdminCondition(
                productId = null,
                productName = null,
                rating = null,
                content = null,
                from = LocalDate.of(2023, 6, 15),
                to = LocalDate.of(2024, 6, 15),
            )

            val reviewAdminInfo1 = ReviewAdminInfo(
                reviewId = 1L,
                rating = 5,
                content = "최고예요",
                adminReply = AdminReply("감사합니다", LocalDateTime.of(2024, 6, 16, 9, 0)),
                user = ReviewAuthor(100L, "사용자1"),
                product = ReviewProduct(1L, "상품1"),
                createdAt = LocalDateTime.of(2024, 6, 1, 12, 0),
            )

            val reviewAdminInfo2 = ReviewAdminInfo(
                reviewId = 2L,
                rating = 2,
                content = "별로예요",
                adminReply = null,
                user = ReviewAuthor(200L, "사용자2"),
                product = ReviewProduct(2L, "상품2"),
                createdAt = LocalDateTime.of(2024, 6, 2, 13, 0),
            )

            val reviewPage = PageImpl(listOf(reviewAdminInfo1, reviewAdminInfo2), pageable, 2)

            every { timeProvider.now() } returns now
            every { reviewAdminReader.searchReviews(expectedCondition, pageable) } returns reviewPage

            val result = adminReviewService.search(request, pageable)

            result.totalElements shouldBe 2
            result.content.size shouldBe 2

            // 첫 번째 리뷰 (관리자 답글 있음)
            val response1 = result.content[0]
            response1.reviewId shouldBe 1L
            response1.adminReply shouldBe SearchReviewAdminReplyResponse("감사합니다", LocalDateTime.of(2024, 6, 16, 9, 0))

            // 두 번째 리뷰 (관리자 답글 없음)
            val response2 = result.content[1]
            response2.reviewId shouldBe 2L
            response2.adminReply shouldBe null
        }
    }
})
