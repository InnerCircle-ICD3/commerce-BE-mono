package com.fastcampus.commerce.cart.application.query

import com.fastcampus.commerce.cart.application.query.dto.CartItemDto

interface CartItemReader {
    fun readCartItems(userId: Long, cartItemIds: Set<Long>): List<CartItemDto>
}
