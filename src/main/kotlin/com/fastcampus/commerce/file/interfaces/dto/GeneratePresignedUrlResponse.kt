package com.fastcampus.commerce.file.interfaces.dto

data class GeneratePresignedUrlResponse(
    val uploadUrl: String,
    val key: String,
    val contextId: String,
)
