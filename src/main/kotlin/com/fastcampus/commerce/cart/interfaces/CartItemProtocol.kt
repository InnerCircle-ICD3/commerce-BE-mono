package com.fastcampus.commerce.cart.interfaces

data class CartCreateRequest(
    val productId: Long,
    val quantity: Int,
)

data class CartCreateResponse(
    val quantity: Int,
    val stockQuantity: Int,
    val requiresQuantityAdjustment: Boolean,
)

data class CartUpdateRequest(
    val cartId: Long,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
)

data class CartUpdateResponse(
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val stockQuantity: Int,
    val requiresQuantityAdjustment: Boolean,
)
