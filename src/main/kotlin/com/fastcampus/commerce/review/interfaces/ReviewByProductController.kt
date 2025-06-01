package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.review.interfaces.dto.AdminReply
import com.fastcampus.commerce.review.interfaces.dto.RatingDistribution
import com.fastcampus.commerce.review.interfaces.dto.ReviewProductResponse
import com.fastcampus.commerce.review.interfaces.dto.ReviewRatingDistribution
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class ReviewByProductController {
    @GetMapping("/products/{productId}/reviews")
    fun getReviewByProduct(
        @PathVariable productId: Long,
        pageable: Pageable,
    ): PagedData<ReviewProductResponse> {
        val reviews = listOf(
            ReviewProductResponse(
                reviewId = 1,
                rating = 5,
                content = "굿",
                createdAt = LocalDateTime.now(),
                adminReply = AdminReply("ㅎㅎ", LocalDateTime.now()),
                productId = 1,
                productName = "스타벅스 캡슐",
                productThumbnail = "https://example.com/thumbnail.jpg",
            ),
        )
        return PagedData.of(PageImpl(reviews, pageable, 1))
    }

    @GetMapping("/products/{productId}/reviews/rating")
    fun getReviewRatingDistribution(
        @PathVariable productId: Long,
    ): ReviewRatingDistribution {
        return ReviewRatingDistribution(
            averageRating = 3.5,
            ratingDistribution = RatingDistribution(
                1,
                2,
                3,
                4,
                5,
            ),
        )
    }
}
