package com.fastcampus.commerce.review.domain.entity

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ReviewTest : FunSpec({
    val now = LocalDateTime.of(2025, 6, 4, 12, 0)

    test("배송일자가 null이면 ORDER_NOT_DELIVERED 예외가 발생한다.") {
        shouldThrow<CoreException> {
            Review.validateReviewWrittenDate(now, null)
        }.errorCode shouldBe ReviewErrorCode.ORDER_NOT_DELIVERED
    }

    test("리뷰 작성 기한이 지난 경우 TOO_LATE 예외가 발생한다.") {
        val deliveredAt = now.minusDays(Review.MAX_WRITTEN_DATE + 1)

        shouldThrow<CoreException> {
            Review.validateReviewWrittenDate(now, deliveredAt)
        }.errorCode shouldBe ReviewErrorCode.TOO_LATE
    }

    test("리뷰 작성 기한 이내이면 예외 발생하지 않는다.") {
        val deliveredAt = now.minusDays(Review.MAX_WRITTEN_DATE - 1)

        Review.validateReviewWrittenDate(now, deliveredAt)
    }
})
