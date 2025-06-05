package com.fastcampus.commerce.review.application

import com.fastcampus.commerce.review.application.response.ProductReviewResponse
import com.fastcampus.commerce.review.domain.repository.ProductReviewRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProductReviewService(
    private val productReviewRepository: ProductReviewRepository,
) {
    fun getProductReviews(productId: Long, pageable: Pageable): Page<ProductReviewResponse> {
        val productReviews = productReviewRepository.getProductReviews(productId, pageable)
        return productReviews.map(ProductReviewResponse::from)
    }
}
