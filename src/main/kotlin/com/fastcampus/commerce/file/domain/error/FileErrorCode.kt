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
    INVALID_FILE_TYPE("FIE-003", "허용되지 않는 파일타입니다.", LogLevel.WARN),
    INVALID_FILE_NAME("FIE-004", "유효하지 않은 파일명입니다.", LogLevel.WARN),
    FILE_NAME_TOO_LONG("FIE-005", "파일 이름은 최대 255자까지만 허용됩니다.", LogLevel.WARN),
    INVALID_DOMAIN_TYPE("FIE-006", "지원하지 않는 도메인입니다.", LogLevel.WARN),
    INVALID_STORAGE_CONTEXT_KEY("FIE-007", "유효하지 않은 업로드 요청입니다.", LogLevel.WARN),
    S3_CLIENT_ERROR("FIE-008", "업로드 연결 요청에 실패했습니다.", LogLevel.WARN),
    S3_BUCKET_NOT_FOUND("FIE-009", "업로드 서비스 처리 중 오류가 발생했습니다.", LogLevel.WARN),
    S3_ACCESS_DENIED("FIE-009", "업로드 서비스 처리 중 오류가 발생했습니다.", LogLevel.WARN),
    S3_SERVER_ERROR("FIE-009", "업로드 서비스 처리 중 오류가 발생했습니다.", LogLevel.WARN),
    FAIL_GENERATE_PRESIGNED_URL("FIE-009", "업로드 요청 중 오류가 발생했습니다.", LogLevel.WARN),
}
