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

data class CartDeleteRequest(
    val productIds: List<Long>
)

data class CartDeleteResponse(
    val message: String
)

data class CartRetrievesResponse(
    val totalPrice: Int,
    val deliveryPrice: Int,
    val cartItems: List<CartItemRetrieve>,
)

data class CartItemRetrieve(
    val cartItemId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Int,
    val stockQuantity: Int,
    val thumbnail: String,
    val isAvailable: Boolean,
)
