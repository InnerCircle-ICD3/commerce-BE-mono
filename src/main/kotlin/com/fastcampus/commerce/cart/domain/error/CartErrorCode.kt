package com.fastcampus.commerce.cart.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class CartErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    EMPTY_PRODUCT_IDS("CRT-001", "Product IDs cannot be empty", LogLevel.WARN),
    CART_ITEMS_NOT_FOUND("CRT-002", "No cart items found for the given product IDs", LogLevel.WARN),
}

