package com.fastcampus.commerce.review.domain.repository

import com.fastcampus.commerce.review.domain.model.ProductReview
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductReviewRepository {
    fun getProductReviews(productId: Long, pageable: Pageable): Page<ProductReview>
}
