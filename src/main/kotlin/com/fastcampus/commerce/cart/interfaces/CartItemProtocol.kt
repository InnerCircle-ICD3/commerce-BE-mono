package com.fastcampus.commerce.cart.interfaces

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive

data class CartCreateRequest(
    @field:Positive(message = "상품 ID는 양수여야 합니다.")
    val productId: Long,
    @field:Positive(message = "수량은 최소 1개 이상이어야 합니다.")
    val quantity: Int,
)

data class CartCreateResponse(
    val quantity: Int,
    val stockQuantity: Int,
    val requiresQuantityAdjustment: Boolean,
)

data class CartUpdateRequest(
    @field:Positive(message = "수량은 최소 1개 이상이어야 합니다.")
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
    @field:NotEmpty(message = "삭제할 장바구니 아이디가 누락되었습니다.")
    val cartItemIds: List<Long>,
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
