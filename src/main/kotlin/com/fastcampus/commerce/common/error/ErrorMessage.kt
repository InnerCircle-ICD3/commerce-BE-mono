package com.fastcampus.commerce.common.error

data class ErrorMessage(
    val code: String,
    val message: String,
) {
    constructor(errorCode: ErrorCode) : this(
        code = errorCode.code,
        message = errorCode.message,
    )

    constructor(errorCode: ErrorCode, message: String?) : this(
        code = errorCode.code,
        message = message ?: errorCode.message,
    )
}
