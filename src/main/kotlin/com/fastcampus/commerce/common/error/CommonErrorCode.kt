package com.fastcampus.commerce.common.error

enum class CommonErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    SERVER_ERROR("SYS-001", "An unexpected error has occurred.", LogLevel.ERROR),
}
