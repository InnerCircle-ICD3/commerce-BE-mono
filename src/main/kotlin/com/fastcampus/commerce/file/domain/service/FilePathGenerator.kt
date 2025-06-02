package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.domain.model.FilePath

interface FilePathGenerator {
    fun generate(contextKeyId: String, request: GeneratePresignedUrlRequest): FilePath
}
