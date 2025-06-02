package com.fastcampus.commerce.common.error

class CoreException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
) : RuntimeException(message)
