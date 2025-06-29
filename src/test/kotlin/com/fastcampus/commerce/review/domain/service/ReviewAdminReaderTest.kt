package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.entity.ReviewReply
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.domain.model.AdminReply
import com.fastcampus.commerce.review.domain.model.ReviewAuthor
import com.fastcampus.commerce.review.domain.model.ReviewInfoFlat
import com.fastcampus.commerce.review.domain.model.ReviewProduct
import com.fastcampus.commerce.review.domain.model.SearchReviewAdminCondition
import com.fastcampus.commerce.review.domain.repository.ReviewAdminRepository
import io.kotest.assertions.throwables.shouldThrow
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

            val flatReview = ReviewInfoFlat(
                reviewId = 1L,
                rating = 5,
                content = "정말 좋은 상품입니다",
                adminReplyContent = "감사합니다",
                adminReplyCreatedAt = adminReplyCreatedAt,
                userId = 100L,
                userNickname = "사용자1",
                productId = 1L,
                productName = "테스트 상품",
                productThumbnail = "http://localhost:8080/api/v1/product/100/thumbnail",
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

            val flatReview = ReviewInfoFlat(
                reviewId = 2L,
                rating = 3,
                content = "보통입니다",
                adminReplyContent = null,
                adminReplyCreatedAt = null,
                userId = 200L,
                userNickname = "사용자2",
                productId = 2L,
                productName = "다른 상품",
                productThumbnail = "http://localhost:8080/api/v1/product/100/thumbnail",
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

        test("관리자 답글 생성일시만 있고 내용이 없는 경우 답글을 null로 처리한다") {
            val reviewCreatedAt = LocalDateTime.of(2024, 6, 10, 14, 20)
            val adminReplyCreatedAt = LocalDateTime.of(2024, 6, 15, 10, 30)

            val flatReview = ReviewInfoFlat(
                reviewId = 4L,
                rating = 2,
                content = "별로입니다",
                adminReplyContent = null,
                adminReplyCreatedAt = adminReplyCreatedAt,
                userId = 400L,
                userNickname = "사용자4",
                productId = 4L,
                productName = "상품4",
                productThumbnail = "http://localhost:8080/api/v1/product/100/thumbnail",
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

            val flatReview1 = ReviewInfoFlat(
                reviewId = 1L,
                rating = 5,
                content = "좋습니다",
                adminReplyContent = "감사합니다",
                adminReplyCreatedAt = adminReplyCreatedAt,
                userId = 100L,
                userNickname = "사용자1",
                productId = 1L,
                productName = "상품1",
                productThumbnail = "http://localhost:8080/api/v1/product/100/thumbnail",
                createdAt = reviewCreatedAt1,
            )

            val flatReview2 = ReviewInfoFlat(
                reviewId = 2L,
                rating = 3,
                content = "보통입니다",
                adminReplyContent = null,
                adminReplyCreatedAt = null,
                userId = 200L,
                userNickname = "사용자2",
                productId = 2L,
                productName = "상품2",
                productThumbnail = "http://localhost:8080/api/v1/product/100/thumbnail",
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
            val emptyPage = PageImpl<ReviewInfoFlat>(emptyList(), pageable, 0)

            every { reviewAdminRepository.searchReviews(condition, pageable) } returns emptyPage

            val result = reviewAdminReader.searchReviews(condition, pageable)

            result.totalElements shouldBe 0
            result.content.size shouldBe 0
            result.isEmpty shouldBe true
        }
    }

    context("getReview") {
        test("리뷰 ID로 관리자 답글이 있는 리뷰를 조회할 수 있다") {
            val reviewId = 1L
            val adminReplyCreatedAt = LocalDateTime.of(2024, 6, 15, 10, 30)
            val reviewCreatedAt = LocalDateTime.of(2024, 6, 10, 14, 20)

            val flatReview = ReviewInfoFlat(
                reviewId = reviewId,
                rating = 5,
                content = "정말 좋은 상품입니다",
                adminReplyContent = "감사합니다",
                adminReplyCreatedAt = adminReplyCreatedAt,
                userId = 100L,
                userNickname = "사용자1",
                productId = 1L,
                productName = "테스트 상품",
                productThumbnail = "http://localhost:8080/api/v1/product/100/thumbnail",
                createdAt = reviewCreatedAt,
            )

            every { reviewAdminRepository.getReview(reviewId) } returns flatReview

            val result = reviewAdminReader.getReview(reviewId)

            result.reviewId shouldBe reviewId
            result.rating shouldBe 5
            result.content shouldBe "정말 좋은 상품입니다"
            result.adminReply shouldBe AdminReply("감사합니다", adminReplyCreatedAt)
            result.user shouldBe ReviewAuthor(100L, "사용자1")
            result.product shouldBe ReviewProduct(1L, "테스트 상품")
            result.createdAt shouldBe reviewCreatedAt
        }

        test("리뷰 ID로 관리자 답글이 없는 리뷰를 조회할 수 있다") {
            val reviewId = 2L
            val reviewCreatedAt = LocalDateTime.of(2024, 6, 10, 14, 20)

            val flatReview = ReviewInfoFlat(
                reviewId = reviewId,
                rating = 3,
                content = "보통입니다",
                adminReplyContent = null,
                adminReplyCreatedAt = null,
                userId = 200L,
                userNickname = "사용자2",
                productId = 2L,
                productName = "다른 상품",
                productThumbnail = "http://localhost:8080/api/v1/product/100/thumbnail",
                createdAt = reviewCreatedAt,
            )

            every { reviewAdminRepository.getReview(reviewId) } returns flatReview

            val result = reviewAdminReader.getReview(reviewId)

            result.reviewId shouldBe reviewId
            result.rating shouldBe 3
            result.content shouldBe "보통입니다"
            result.adminReply shouldBe null
            result.user shouldBe ReviewAuthor(200L, "사용자2")
            result.product shouldBe ReviewProduct(2L, "다른 상품")
            result.createdAt shouldBe reviewCreatedAt
        }

        test("존재하지 않는 리뷰 ID로 조회하면 REVIEW_NOT_FOUND 예외가 발생한다") {
            val reviewId = 999L

            every { reviewAdminRepository.getReview(reviewId) } returns null

            shouldThrow<CoreException> {
                reviewAdminReader.getReview(reviewId)
            }.errorCode shouldBe ReviewErrorCode.REVIEW_NOT_FOUND
        }
    }

    context("getReply") {
        test("답글 ID로 답글을 조회할 수 있다") {
            val replyId = 1L
            val reviewReply = ReviewReply(
                reviewId = 100L,
                replierId = 1L,
                content = "감사합니다. 좋은 리뷰 남겨주셔서 기쁩니다.",
            ).apply {
                id = replyId
            }

            every { reviewAdminRepository.findReply(replyId) } returns java.util.Optional.of(reviewReply)

            val result = reviewAdminReader.getReply(replyId)

            result.id shouldBe replyId
            result.reviewId shouldBe 100L
            result.replierId shouldBe 1L
            result.content shouldBe "감사합니다. 좋은 리뷰 남겨주셔서 기쁩니다."
        }

        test("존재하지 않는 답글 ID로 조회하면 REPLY_NOT_FOUND 예외가 발생한다") {
            val replyId = 999L

            every { reviewAdminRepository.findReply(replyId) } returns java.util.Optional.empty()

            shouldThrow<CoreException> {
                reviewAdminReader.getReply(replyId)
            }.errorCode shouldBe ReviewErrorCode.REPLY_NOT_FOUND
        }
    }
})
