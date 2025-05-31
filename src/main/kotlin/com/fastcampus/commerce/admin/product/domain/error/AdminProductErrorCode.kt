package com.fastcampus.commerce.admin.product.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class AdminProductErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    PRODUCT_NAME_EMPTY("APD-001", "상품명이 비어있습니다.", LogLevel.WARN),
    PRODUCT_NAME_TOO_LONG("APD-002", "상품명은 최대 255자까지 입력가능합니다.", LogLevel.WARN),
    PRODUCT_PRICE_NOT_POSITIVE("APD-003", "상품가격은 0보다 커야합니다.", LogLevel.WARN),
    PRODUCT_QUANTITY_NEGATIVE("APD-004", "상품재고는 0이상이어야합니다.", LogLevel.WARN),
    PRODUCT_THUMBNAIL_EMPTY("APD-005", "상품 썸네일이 비어있습니다.", LogLevel.WARN),
    PRODUCT_DETAIL_IMAGE_EMPTY("APD-006", "상품 상세이미지가 비어있습니다.", LogLevel.WARN),
    PRODUCT_CATEGORY_NOT_EXISTS("APD-007", "유효하지 않은 카테고리(강도/컵사이즈)입니다.", LogLevel.WARN),

    PRODUCT_NOT_EXISTS("APD-008", "존재하지 않는 상품입니다.", LogLevel.WARN),
}
