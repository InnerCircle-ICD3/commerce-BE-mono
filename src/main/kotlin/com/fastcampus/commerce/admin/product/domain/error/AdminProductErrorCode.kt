package com.fastcampus.commerce.admin.product.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class AdminProductErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    PRODUCT_NAME_EMPTY("APD-001", "Product Name is Empty.", LogLevel.WARN),
}
