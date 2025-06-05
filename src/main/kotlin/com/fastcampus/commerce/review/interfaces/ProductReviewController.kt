package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.review.application.ProductReviewService
import com.fastcampus.commerce.review.application.response.ProductReviewResponse
import com.fastcampus.commerce.review.interfaces.response.ProductReviewApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/products")
@RestController
class ProductReviewController(
    private val productReviewService: ProductReviewService,
) {
    @GetMapping("/{productId}/reviews")
    fun getProductReviews(
        @PathVariable productId: Long,
        @PageableDefault pageable: Pageable,
    ): PagedData<ProductReviewApiResponse> {
        val productReviews: Page<ProductReviewResponse> = productReviewService.getProductReviews(productId, pageable)
        return PagedData.of(productReviews.map(ProductReviewApiResponse::from))
    }
}
