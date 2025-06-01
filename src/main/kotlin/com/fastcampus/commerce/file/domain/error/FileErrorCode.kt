package com.fastcampus.commerce.file.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class FileErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    FILE_NAME_EMPTY("FIE-001", "파일명이 비어있습니다.", LogLevel.WARN),
    FILE_SIZE_TOO_LARGE("FIE-002", "파일크기가 너무 큽니다.", LogLevel.WARN),
    INVALID_FILE_POLICY("FIE-003", "허용되지 않는 파일타입입니다.", LogLevel.WARN),
}
