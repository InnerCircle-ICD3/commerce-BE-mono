package com.fastcampus.commerce.order.domain.repository

import com.fastcampus.commerce.order.application.review.OrderReview

interface OrderReviewRepository {
    fun findOrderReview(orderNumber: String, orderItemId: Long): OrderReview?
}
