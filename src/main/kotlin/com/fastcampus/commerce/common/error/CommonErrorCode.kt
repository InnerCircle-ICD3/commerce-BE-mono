package com.fastcampus.commerce.common.error

enum class CommonErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    SERVER_ERROR("SYS-001", "시스템 오류가 발생했습니다.", LogLevel.ERROR),

    FIELD_ERROR("FED-001", "올바르지 않은 요청입니다.", LogLevel.WARN),
}
