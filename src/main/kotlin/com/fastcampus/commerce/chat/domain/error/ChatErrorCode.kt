package com.fastcampus.commerce.chat.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class ChatErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    NOT_FOUND("CHT-001", "해당 채팅방이 없습니다", LogLevel.ERROR),
    BAD_REQUEST("CHT-002", "채팅방이 종료되었습니다.", LogLevel.ERROR),
}
