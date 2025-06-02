package com.fastcampus.commerce.file.application

import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.application.response.GeneratePresignedUrlResponse
import com.fastcampus.commerce.file.domain.service.FileStore
import com.fastcampus.commerce.file.domain.service.UploadUrlGenerator
import com.fastcampus.commerce.file.domain.validator.FileValidator
import org.springframework.stereotype.Service

@Service
class FileService(
    private val fileValidator: FileValidator,
    private val fileStore: FileStore,
    private val uploadUrlGenerator: UploadUrlGenerator,
) {
    fun generatePresignedUrl(uploaderId: Long, request: GeneratePresignedUrlRequest): GeneratePresignedUrlResponse {
        fileValidator.validate(request)
        val fileMetadata = fileStore.prepareGeneratePresignedUrl(uploaderId, request)
        val uploadUrl = uploadUrlGenerator.generate(fileMetadata)
        return GeneratePresignedUrlResponse(uploadUrl, fileMetadata.storedPath, fileMetadata.contextKey)
    }
}
