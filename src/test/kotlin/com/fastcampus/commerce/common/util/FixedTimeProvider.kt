package com.fastcampus.commerce.common.util

import java.time.LocalDateTime

class FixedTimeProvider : TimeProvider {
    override fun now(): LocalDateTime = LocalDateTime.of(2025, 6, 4, 11, 39, 22)
}
