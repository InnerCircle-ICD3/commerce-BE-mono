package com.fastcampus.commerce.common.util

import java.time.LocalDateTime

interface TimeProvider {
    fun now(): LocalDateTime
}
