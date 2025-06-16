package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.review.application.ProductReviewService
import com.fastcampus.commerce.review.application.response.ProductReviewRatingResponse
import com.fastcampus.commerce.review.application.response.ProductReviewResponse
import com.fastcampus.commerce.review.interfaces.response.ProductReviewApiResponse
import com.fastcampus.commerce.review.interfaces.response.ProductReviewRatingApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductReviewController(
    private val productReviewService: ProductReviewService,
) {
    @GetMapping("/reviews:byProduct")
    fun getProductReviews(
        @RequestParam productId: Long,
        @PageableDefault pageable: Pageable,
    ): PagedData<ProductReviewApiResponse> {
        val productReviews: Page<ProductReviewResponse> = productReviewService.getProductReviews(productId, pageable)
        return PagedData.of(productReviews.map(ProductReviewApiResponse::from))
    }

    @GetMapping("/reviews/rating:byProduct")
    fun getProductReviewRating(
        @RequestParam productId: Long,
    ): ProductReviewRatingApiResponse {
        val reviewRating: ProductReviewRatingResponse = productReviewService.getProductReviewRating(productId)
        return ProductReviewRatingApiResponse.from(reviewRating)
    }
}
