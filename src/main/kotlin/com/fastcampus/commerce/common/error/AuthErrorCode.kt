package com.fastcampus.commerce.common.error

enum class AuthErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    INVALID_TOKEN("AUTH-001", "Invalid token", LogLevel.WARN),
    EXPIRED_TOKEN("AUTH-002", "Expired token", LogLevel.WARN),
    TOKEN_NOT_FOUND("AUTH-003", "Token not found", LogLevel.WARN),
}
