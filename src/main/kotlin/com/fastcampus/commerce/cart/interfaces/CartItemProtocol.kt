package com.fastcampus.commerce.cart.interfaces

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CartCreateRequest(
    @field:NotNull(message = "상품 ID는 필수입니다.")
    @field:Positive(message = "상품 ID는 양수여야 합니다.")
    val productId: Long,
    @field:NotNull(message = "수량은 필수입니다. ")
    @field:Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    val quantity: Int,
)

data class CartCreateResponse(
    val quantity: Int,
    val stockQuantity: Int,
    val requiresQuantityAdjustment: Boolean,
)

data class CartUpdateRequest(
    @field:NotNull(message = "수량은 필수입니다.")
    @field:Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    val quantity: Int,
)

data class CartUpdateResponse(
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val stockQuantity: Int,
    val requiresQuantityAdjustment: Boolean,
)

data class CartDeleteResponse(
    val message: String,
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
