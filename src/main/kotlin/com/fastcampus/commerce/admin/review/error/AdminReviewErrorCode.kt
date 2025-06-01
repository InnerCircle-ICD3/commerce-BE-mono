package com.fastcampus.commerce.admin.review.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class AdminReviewErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    REVIEW_NOT_EXISTS("ARV-001", "존재하지 않는 리뷰입니다.", LogLevel.WARN),
    REPLY_CONTENT_EMPTY("ARV-002", "리뷰 답글이 비어있습니다.", LogLevel.WARN),
    REPLY_NOT_EXISTS("ARV-003", "존재하지 않는 리뷰 답글입니다.", LogLevel.WARN),
}
