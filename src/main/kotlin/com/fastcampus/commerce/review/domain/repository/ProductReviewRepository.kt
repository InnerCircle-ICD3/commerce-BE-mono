package com.fastcampus.commerce.review.domain.repository

import com.fastcampus.commerce.review.domain.model.ProductReview
import com.fastcampus.commerce.review.domain.model.ProductReviewFlat
import com.fastcampus.commerce.review.domain.model.ProductReviewRating
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductReviewRepository {
    fun getProductReviews(productId: Long, pageable: Pageable): Page<ProductReviewFlat>

    fun getProductReviewRating(productId: Long): ProductReviewRating?
}
