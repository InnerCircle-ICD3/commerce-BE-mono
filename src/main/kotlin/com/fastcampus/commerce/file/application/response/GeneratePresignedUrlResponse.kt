package com.fastcampus.commerce.file.application.response

import java.util.UUID

data class GeneratePresignedUrlResponse(
    val uploadUrl: String,
    val key: String,
    val contextId: UUID,
)
