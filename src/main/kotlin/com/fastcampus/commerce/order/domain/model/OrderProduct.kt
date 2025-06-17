package com.fastcampus.commerce.order.domain.model

data class OrderProduct (
    val orderItemId: Long,
    val productId: Long,
    val quantity: Int,
)
