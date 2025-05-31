package com.fastcampus.commerce.product.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class ProductErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    PRODUCT_NOT_FOUND("PRO-001", "Product Not Found.", LogLevel.ERROR),
}
