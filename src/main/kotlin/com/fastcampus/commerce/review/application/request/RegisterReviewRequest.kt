package com.fastcampus.commerce.review.application.request

import com.fastcampus.commerce.order.application.review.OrderReview
import com.fastcampus.commerce.review.domain.model.ReviewRegister

data class RegisterReviewRequest(
    val orderNumber: String,
    val orderItemId: Long,
    val rating: Int,
    val content: String,
) {
    fun toCommand(userId: Long, orderReview: OrderReview): ReviewRegister =
        ReviewRegister(
            userId = userId,
            orderItemId = orderItemId,
            productId = orderReview.productId,
            rating = rating,
            content = content,
            deliveredAt = orderReview.deliveredAt,
        )
}
