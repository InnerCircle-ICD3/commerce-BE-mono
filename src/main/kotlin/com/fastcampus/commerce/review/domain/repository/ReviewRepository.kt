package com.fastcampus.commerce.review.domain.repository

interface ReviewRepository {
    fun existsByUserIdAndOrderItemId(userId: Long, orderItemId: Long): Boolean
}
