package com.fastcampus.commerce.review.application

import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.review.application.request.UserReviewRequest
import com.fastcampus.commerce.review.domain.model.ReviewInfoFlat
import com.fastcampus.commerce.review.domain.model.SearchUserReviewCondition
import com.fastcampus.commerce.review.domain.repository.UserReviewRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

class UserReviewServiceTest : DescribeSpec({

    val timeProvider = mockk<TimeProvider>()
    val userReviewRepository = mockk<UserReviewRepository>()
    val userReviewService = UserReviewService(timeProvider, userReviewRepository)

    beforeTest {
        clearAllMocks()
    }

    describe("사용자 리뷰 목록 조회") {
        val userId = 1L
        val now = LocalDateTime.of(2025, 6, 14, 12, 0)
        val pageable = PageRequest.of(0, 10)

        context("기간 없이 조회") {
            it("사용자가 작성한 모든 리뷰를 조회할 수 있다") {
                val request = UserReviewRequest(userId = userId, period = null)
                val condition = SearchUserReviewCondition(userId = userId, from = null, to = null)

                val reviews = listOf(
                    ReviewInfoFlat(
                        reviewId = 1L,
                        rating = 5,
                        content = "최고의 제품입니다!",
                        adminReplyContent = "감사합니다!",
                        adminReplyCreatedAt = now.minusDays(1),
                        userId = userId,
                        userNickname = "user1",
                        productId = 100L,
                        productName = "상품A",
                        productThumbnail = "http://localhost:8080/api/v1/product/100/thumbnail",
                        createdAt = now.minusDays(2),
                    ),
                    ReviewInfoFlat(
                        reviewId = 2L,
                        rating = 4,
                        content = "만족합니다",
                        adminReplyContent = null,
                        adminReplyCreatedAt = null,
                        userId = userId,
                        userNickname = "user1",
                        productId = 200L,
                        productName = "상품B",
                        productThumbnail = "http://localhost:8080/api/v1/product/100/thumbnail",
                        createdAt = now.minusDays(5),
                    ),
                )
                val reviewPage = PageImpl(reviews, pageable, reviews.size.toLong())

                every { timeProvider.now() } returns now
                every { userReviewRepository.getReviewsBy(condition, pageable) } returns reviewPage

                val result = userReviewService.getReviewsByAuthor(request, pageable)

                result.content.size shouldBe 2
                result.content[0].reviewId shouldBe 1L
                result.content[0].rating shouldBe 5
                result.content[0].content shouldBe "최고의 제품입니다!"
                result.content[0].adminReply?.content shouldBe "감사합니다!"
                result.content[0].adminReply?.createdAt shouldBe now.minusDays(1)
                result.content[0].product.productId shouldBe 100L
                result.content[0].product.productName shouldBe "상품A"
                result.content[0].product.productThumbnail shouldBe "http://localhost:8080/api/v1/product/100/thumbnail"
                result.content[0].createdAt shouldBe now.minusDays(2)

                result.content[1].reviewId shouldBe 2L
                result.content[1].rating shouldBe 4
                result.content[1].content shouldBe "만족합니다"
                result.content[1].adminReply shouldBe null
                result.content[1].product.productId shouldBe 200L
                result.content[1].product.productName shouldBe "상품B"
                result.content[1].product.productThumbnail shouldBe "http://localhost:8080/api/v1/product/100/thumbnail"
                result.content[1].createdAt shouldBe now.minusDays(5)

                verify(exactly = 1) { timeProvider.now() }
                verify(exactly = 1) { userReviewRepository.getReviewsBy(condition, pageable) }
            }
        }

        context("기간을 지정하여 조회") {
            it("지정된 기간 내의 리뷰만 조회할 수 있다") {
                val period = 3
                val request = UserReviewRequest(userId = userId, period = period)
                val toDate = now.toLocalDate()
                val fromDate = toDate.minusMonths(period.toLong())
                val condition = SearchUserReviewCondition(userId = userId, from = fromDate, to = toDate)

                val reviews = listOf(
                    ReviewInfoFlat(
                        reviewId = 3L,
                        rating = 3,
                        content = "보통이에요",
                        adminReplyContent = null,
                        adminReplyCreatedAt = null,
                        userId = userId,
                        userNickname = "user1",
                        productId = 300L,
                        productName = "상품C",
                        productThumbnail = "http://localhost:8080/api/v1/product/100/thumbnail",
                        createdAt = now.minusMonths(1),
                    ),
                )
                val reviewPage = PageImpl(reviews, pageable, reviews.size.toLong())

                every { timeProvider.now() } returns now
                every { userReviewRepository.getReviewsBy(condition, pageable) } returns reviewPage

                val result = userReviewService.getReviewsByAuthor(request, pageable)

                result.content.size shouldBe 1
                result.content[0].reviewId shouldBe 3L
                result.content[0].rating shouldBe 3
                result.content[0].content shouldBe "보통이에요"
                result.content[0].product.productId shouldBe 300L
                result.content[0].product.productName shouldBe "상품C"

                verify(exactly = 1) { timeProvider.now() }
                verify(exactly = 1) { userReviewRepository.getReviewsBy(condition, pageable) }
            }
        }

        context("리뷰가 없을 때") {
            it("빈 페이지를 반환한다") {
                val request = UserReviewRequest(userId = userId, period = null)
                val condition = SearchUserReviewCondition(userId = userId, from = null, to = null)
                val emptyPage = PageImpl<ReviewInfoFlat>(emptyList(), pageable, 0)

                every { timeProvider.now() } returns now
                every { userReviewRepository.getReviewsBy(condition, pageable) } returns emptyPage

                val result = userReviewService.getReviewsByAuthor(request, pageable)

                result.content.size shouldBe 0
                result.totalElements shouldBe 0

                verify(exactly = 1) { timeProvider.now() }
                verify(exactly = 1) { userReviewRepository.getReviewsBy(condition, pageable) }
            }
        }
    }
})
