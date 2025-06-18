package com.fastcampus.commerce.cart.application.query.dto

import com.fastcampus.commerce.cart.domain.entity.CartItem

data class CartItemDto(
    val cartItemId: Long,
    val productId: Long,
    val quantity: Int,
) {
    companion object {
        fun from(cartItem: CartItem): CartItemDto {
            return CartItemDto(
                cartItemId = cartItem.id!!,
                productId = cartItem.productId,
                quantity = cartItem.quantity,
            )
        }
    }
}
