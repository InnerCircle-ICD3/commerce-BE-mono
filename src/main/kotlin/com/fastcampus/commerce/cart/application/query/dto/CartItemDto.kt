package com.fastcampus.commerce.cart.application.query.dto

import com.fastcampus.commerce.cart.domain.entity.CartItem

data class CartItemDto(
    val id: Long? = null,
    val productId: Long,
    val productName: String? = null,
    val quantity: Int,
    val unitPrice: Int? = null,
    val productSnapshotId: Long? = null,
) {
    companion object {
        fun from(cartItem: CartItem): CartItemDto {
            return CartItemDto(
                id = cartItem.id,
                productId = cartItem.productId,
                productName = null,
                quantity = cartItem.quantity,
                unitPrice = null,
                productSnapshotId = null,
            )
        }
    }
}
