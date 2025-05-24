package com.fastcampus.commerce.common.error

interface ErrorCode {
    val code: String
    val message: String
    val logLevel: LogLevel
}

enum class LogLevel {
    INFO,
    WARN,
    ERROR,
}
