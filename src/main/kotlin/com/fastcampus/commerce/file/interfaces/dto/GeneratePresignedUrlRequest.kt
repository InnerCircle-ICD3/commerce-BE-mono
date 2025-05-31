package com.fastcampus.commerce.file.interfaces.dto

data class GeneratePresignedUrlRequest(
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
    val domainType: String,
    val domainContext: String,
    val contextId: String? = null,
)
