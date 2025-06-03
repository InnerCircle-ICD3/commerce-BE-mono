package com.fastcampus.commerce.product.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class ProductErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    PRODUCT_NOT_FOUND("PRO-001", "상품을 찾을 수 없습니다.", LogLevel.WARN),
    INVENTORY_NOT_FOUND("PRO-002", "상품 재고를 찾을 수 없습니다.", LogLevel.WARN),

    PRODUCT_NAME_EMPTY("PRD-101", "상품명이 비어있습니다.", LogLevel.WARN),
    PRODUCT_NAME_TOO_LONG("PRD-102", "상품명이 너무 깁니다.", LogLevel.WARN),
    PRICE_NOT_POSITIVE("PRD-103", "가격은 양수여야합니다.", LogLevel.WARN),
    QUANTITY_NEGATIVE("PRD-104", "재고는 음수일 수 없습니다.", LogLevel.WARN),
    INVALID_CATEGORY("PRD-105", "유효하지 않은 카테고리입니다.", LogLevel.WARN),
}
