package com.fastcampus.commerce.user.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class UserErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    USER_NOT_FOUND("USR-001", "유저를 찾을 수 없습니다.", LogLevel.WARN),

    USER_ADDRESS_NOT_FOUND("USR-101", "유저 배송지를 찾을 수 없습니다.", LogLevel.WARN),
    UNAUTHORIZED_USER_ADDRESS_UPDATE("USR-102", "본인의 배송지만 변경할 수 있습니다.", LogLevel.WARN),
}
