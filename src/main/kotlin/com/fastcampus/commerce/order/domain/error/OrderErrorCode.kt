package com.fastcampus.commerce.order.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class OrderErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    ORDER_DATA_FOR_REVIEW_NOT_FOUND("ORD-501", "리뷰 작성을 위한 주문데이터를 찾을 수 없습니다.", LogLevel.WARN),
}
