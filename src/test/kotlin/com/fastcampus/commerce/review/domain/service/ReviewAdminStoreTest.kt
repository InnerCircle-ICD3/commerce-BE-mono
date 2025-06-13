package com.fastcampus.commerce.review.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.entity.ReviewReply
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import com.fastcampus.commerce.review.domain.model.AdminReply
import com.fastcampus.commerce.review.domain.model.ReviewAdminInfo
import com.fastcampus.commerce.review.domain.model.ReviewAuthor
import com.fastcampus.commerce.review.domain.model.ReviewProduct
import com.fastcampus.commerce.review.domain.repository.ReviewAdminRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class ReviewAdminStoreTest : FunSpec(
    {

        val reviewAdminRepository = mockk<ReviewAdminRepository>()
        val reviewAdminStore = ReviewAdminStore(reviewAdminRepository)

        beforeEach {
            clearAllMocks()
        }

        context("registerReply") {
            val replierId = 1L
            val reviewId = 100L
            val replyContent = "감사합니다. 더 좋은 서비스로 보답하겠습니다."
            val reviewCreatedAt = LocalDateTime.of(2024, 6, 10, 14, 20)

            test("관리자 답글이 없는 리뷰에 답글을 등록할 수 있다") {
                val reviewAdminInfo = ReviewAdminInfo(
                    reviewId = reviewId,
                    rating = 5,
                    content = "정말 좋은 상품입니다",
                    adminReply = null,
                    user = ReviewAuthor(100L, "사용자1"),
                    product = ReviewProduct(1L, "테스트 상품"),
                    createdAt = reviewCreatedAt,
                )

                val expectedReviewReply = ReviewReply(
                    reviewId = reviewId,
                    replierId = replierId,
                    content = replyContent,
                ).apply { id = 1L }

                every {
                    reviewAdminRepository.registerReply(
                        match {
                            it.reviewId == reviewId &&
                                it.replierId == replierId &&
                                it.content == replyContent
                        },
                    )
                } returns expectedReviewReply

                val result = reviewAdminStore.registerReply(replierId, reviewAdminInfo, replyContent)

                result.reviewId shouldBe reviewId
                result.replierId shouldBe replierId
                result.content shouldBe replyContent

                verify(exactly = 1) {
                    reviewAdminRepository.registerReply(
                        match {
                            it.reviewId == reviewId &&
                                it.replierId == replierId &&
                                it.content == replyContent
                        },
                    )
                }
            }

            test("이미 관리자 답글이 있는 리뷰에 답글을 등록하려고 하면 REPLY_EXISTS 예외가 발생한다") {
                val adminReplyCreatedAt = LocalDateTime.of(2024, 6, 15, 10, 30)
                val reviewAdminInfo = ReviewAdminInfo(
                    reviewId = reviewId,
                    rating = 5,
                    content = "정말 좋은 상품입니다",
                    adminReply = AdminReply("이미 답변된 리뷰입니다", adminReplyCreatedAt),
                    user = ReviewAuthor(100L, "사용자1"),
                    product = ReviewProduct(1L, "테스트 상품"),
                    createdAt = reviewCreatedAt,
                )

                shouldThrow<CoreException> {
                    reviewAdminStore.registerReply(replierId, reviewAdminInfo, replyContent)
                }.errorCode shouldBe ReviewErrorCode.REPLY_EXISTS

                verify(exactly = 0) { reviewAdminRepository.registerReply(any()) }
            }

            test("빈 내용으로 답글을 등록하려고 하면 REPLY_CONTENT_EMPTY 예외가 발생한다") {
                val reviewAdminInfo = ReviewAdminInfo(
                    reviewId = reviewId,
                    rating = 3,
                    content = "보통입니다",
                    adminReply = null,
                    user = ReviewAuthor(200L, "사용자2"),
                    product = ReviewProduct(2L, "다른 상품"),
                    createdAt = reviewCreatedAt,
                )

                val emptyContent = ""

                shouldThrow<CoreException> {
                    reviewAdminStore.registerReply(replierId, reviewAdminInfo, emptyContent)
                }.errorCode shouldBe ReviewErrorCode.REPLY_CONTENT_EMPTY

                verify(exactly = 0) { reviewAdminRepository.registerReply(any()) }
            }

            test("공백만 있는 내용으로 답글을 등록하려고 하면 REPLY_CONTENT_EMPTY 예외가 발생한다") {
                val reviewAdminInfo = ReviewAdminInfo(
                    reviewId = reviewId,
                    rating = 4,
                    content = "괜찮아요",
                    adminReply = null,
                    user = ReviewAuthor(300L, "사용자3"),
                    product = ReviewProduct(3L, "세 번째 상품"),
                    createdAt = reviewCreatedAt,
                )

                val blankContent = "   "

                shouldThrow<CoreException> {
                    reviewAdminStore.registerReply(replierId, reviewAdminInfo, blankContent)
                }.errorCode shouldBe ReviewErrorCode.REPLY_CONTENT_EMPTY

                verify(exactly = 0) { reviewAdminRepository.registerReply(any()) }
            }

            test("서로 다른 리뷰에 각각 답글을 등록할 수 있다") {
                val reviewId1 = 101L
                val reviewId2 = 102L
                val replierId2 = 2L

                val reviewAdminInfo1 = ReviewAdminInfo(
                    reviewId = reviewId1,
                    rating = 5,
                    content = "첫 번째 리뷰",
                    adminReply = null,
                    user = ReviewAuthor(101L, "사용자A"),
                    product = ReviewProduct(10L, "상품A"),
                    createdAt = reviewCreatedAt,
                )

                val reviewAdminInfo2 = ReviewAdminInfo(
                    reviewId = reviewId2,
                    rating = 4,
                    content = "두 번째 리뷰",
                    adminReply = null,
                    user = ReviewAuthor(102L, "사용자B"),
                    product = ReviewProduct(20L, "상품B"),
                    createdAt = reviewCreatedAt.plusDays(1),
                )

                val reply1 = ReviewReply(
                    reviewId = reviewId1,
                    replierId = replierId,
                    content = "첫 번째 답글",
                ).apply { id = 10L }

                val reply2 = ReviewReply(
                    reviewId = reviewId2,
                    replierId = replierId2,
                    content = "두 번째 답글",
                ).apply { id = 20L }

                every {
                    reviewAdminRepository.registerReply(
                        match { it.reviewId == reviewId1 },
                    )
                } returns reply1

                every {
                    reviewAdminRepository.registerReply(
                        match { it.reviewId == reviewId2 },
                    )
                } returns reply2

                val result1 = reviewAdminStore.registerReply(replierId, reviewAdminInfo1, "첫 번째 답글")
                val result2 = reviewAdminStore.registerReply(replierId2, reviewAdminInfo2, "두 번째 답글")

                result1.reviewId shouldBe reviewId1
                result1.content shouldBe "첫 번째 답글"

                result2.reviewId shouldBe reviewId2
                result2.content shouldBe "두 번째 답글"

                verify(exactly = 2) { reviewAdminRepository.registerReply(any()) }
            }
        }

        context("deleteReply") {
            val adminId = 1L
            val replyId = 100L

            val reply = ReviewReply(
                reviewId = 100L,
                replierId = adminId,
                content = "감사합니다.",
            ).apply { id = replyId }

            test("관리자 답글을 삭제할 수 있다") {
                every {
                    reviewAdminRepository.deleteReply(reply)
                } returns Unit

                reviewAdminStore.deleteReply(adminId, reply)

                verify(exactly = 1) {
                    reviewAdminRepository.deleteReply(reply)
                }
            }
        }
    },
)
