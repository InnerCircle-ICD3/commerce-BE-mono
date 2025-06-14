package com.fastcampus.commerce.admin.review.application.request

import com.fastcampus.commerce.review.domain.model.SearchReviewAdminCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime

class SearchReviewAdminRequestTest : FunSpec({

    context("toCondition") {
        val baseDateTime = LocalDateTime.of(2024, 6, 15, 14, 30, 0)
        val baseDate = LocalDate.of(2024, 6, 15)

        test("period가 null이면 from과 to가 모두 null이다") {
            val request = SearchReviewAdminRequest(
                productId = 1L,
                productName = "테스트 상품",
                rating = 5,
                content = "좋아요",
                period = null,
            )

            val condition = request.toCondition(baseDateTime)

            condition shouldBe SearchReviewAdminCondition(
                productId = 1L,
                productName = "테스트 상품",
                rating = 5,
                content = "좋아요",
                from = null,
                to = null,
            )
        }

        test("period가 3이면 3개월 전부터 현재까지로 설정된다") {
            val request = SearchReviewAdminRequest(
                productId = 1L,
                productName = "테스트 상품",
                rating = 5,
                content = "좋아요",
                period = 3,
            )

            val condition = request.toCondition(baseDateTime)

            condition shouldBe SearchReviewAdminCondition(
                productId = 1L,
                productName = "테스트 상품",
                rating = 5,
                content = "좋아요",
                from = LocalDate.of(2024, 3, 15),
                to = baseDate,
            )
        }

        test("period가 6이면 6개월 전부터 현재까지로 설정된다") {
            val request = SearchReviewAdminRequest(
                period = 6,
            )

            val condition = request.toCondition(baseDateTime)

            condition shouldBe SearchReviewAdminCondition(
                productId = null,
                productName = null,
                rating = null,
                content = null,
                from = LocalDate.of(2023, 12, 15),
                to = baseDate,
            )
        }

        test("period가 9이면 9개월 전부터 현재까지로 설정된다") {
            val request = SearchReviewAdminRequest(
                period = 9,
            )

            val condition = request.toCondition(baseDateTime)

            condition shouldBe SearchReviewAdminCondition(
                productId = null,
                productName = null,
                rating = null,
                content = null,
                from = LocalDate.of(2023, 9, 15),
                to = baseDate,
            )
        }

        test("period가 12이면 12개월 전부터 현재까지로 설정된다") {
            val request = SearchReviewAdminRequest(
                period = 12,
            )

            val condition = request.toCondition(baseDateTime)

            condition shouldBe SearchReviewAdminCondition(
                productId = null,
                productName = null,
                rating = null,
                content = null,
                from = LocalDate.of(2023, 6, 15),
                to = baseDate,
            )
        }

        test("년(year) 경계를 넘나드는 날짜 계산이 올바르게 동작한다") {
            val januaryDateTime = LocalDateTime.of(2024, 1, 31, 10, 0, 0)
            val request = SearchReviewAdminRequest(period = 3)

            val condition = request.toCondition(januaryDateTime)

            condition.from shouldBe LocalDate.of(2023, 10, 31)
            condition.to shouldBe LocalDate.of(2024, 1, 31)
        }

        test("윤년 경계에서 날짜 계산이 올바르게 동작한다") {
            val leapYearDateTime = LocalDateTime.of(2024, 2, 29, 15, 30, 0)
            val request = SearchReviewAdminRequest(period = 12)

            val condition = request.toCondition(leapYearDateTime)

            condition.from shouldBe LocalDate.of(2023, 2, 28) // 작년은 평년이므로 2월 28일
            condition.to shouldBe LocalDate.of(2024, 2, 29)
        }
    }
})
