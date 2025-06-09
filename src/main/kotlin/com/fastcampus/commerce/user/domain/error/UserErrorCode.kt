package com.fastcampus.commerce.user.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class UserErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    USER_NOT_FOUND("USR-001", "유저를 찾을 수 없습니다.", LogLevel.WARN),
}
