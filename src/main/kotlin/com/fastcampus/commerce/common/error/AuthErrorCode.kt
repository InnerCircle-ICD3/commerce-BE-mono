package com.fastcampus.commerce.common.error

enum class AuthErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    INVALID_TOKEN("ATH-001", "Invalid token", LogLevel.WARN),
    EXPIRED_TOKEN("ATH-002", "Expired token", LogLevel.WARN),
    TOKEN_NOT_FOUND("ATH-003", "Token not found", LogLevel.WARN),
}
