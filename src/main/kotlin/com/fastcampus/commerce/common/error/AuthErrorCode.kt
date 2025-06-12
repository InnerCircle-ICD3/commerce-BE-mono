package com.fastcampus.commerce.common.error

enum class AuthErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    INVALID_TOKEN("AUTH-001", "유효하지 않은 토큰입니다.", LogLevel.WARN),
    EXPIRED_TOKEN("AUTH-002", "만료된 토큰입니다.", LogLevel.WARN),
    TOKEN_NOT_FOUND("AUTH-003", "토큰을 찾을 수 없습니다.", LogLevel.WARN),
    UNAUTHENTICATED("AUTH-004", "인증되지 않은 사용자입니다.", LogLevel.WARN),
    USER_NOT_FOUND("AUTH-005", "사용자를 찾을 수 없습니다.", LogLevel.WARN),
    EMAIL_ALREADY_EXISTS("AUTH-007", "이미 등록된 이메일입니다.", LogLevel.WARN),
    ROLE_NOT_FOUND("AUTH-008", "요청하신 권한을 찾을 수 없습니다.", LogLevel.WARN),
}
