package com.fastcampus.commerce.file.domain.model

data class FilePolicy(
    val maxSize: Int,
    val allowedExtensions: Set<String>,
)
