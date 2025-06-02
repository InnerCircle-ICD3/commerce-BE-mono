package com.fastcampus.commerce.file.application.request

import com.fastcampus.commerce.file.domain.model.DomainType
import com.fastcampus.commerce.file.domain.model.FileType

data class GeneratePresignedUrlRequest(
    val fileName: String,
    val contentType: String,
    val fileSize: Int,
    val domainType: DomainType,
    val domainContext: String,
    val fileType: FileType,
    val contextId: String? = null,
)
