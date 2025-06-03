package com.fastcampus.commerce.file.application

import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.application.response.GeneratePresignedUrlResponse
import com.fastcampus.commerce.file.domain.model.UploadUrl
import com.fastcampus.commerce.file.domain.service.FileStore
import com.fastcampus.commerce.file.domain.service.UploadUrlGenerator
import com.fastcampus.commerce.file.domain.validator.FileUploadPolicyValidator
import org.springframework.stereotype.Service

@Service
class FileService(
    private val fileUploadPolicyValidator: FileUploadPolicyValidator,
    private val fileStore: FileStore,
    private val uploadUrlGenerator: UploadUrlGenerator,
) {
    fun generatePresignedUrl(uploaderId: Long, request: GeneratePresignedUrlRequest): GeneratePresignedUrlResponse {
        fileUploadPolicyValidator.validate(request)
        val fileMetadata = fileStore.prepareGeneratePresignedUrl(uploaderId, request)
        val uploadUrl = uploadUrlGenerator.generate(fileMetadata)
        return GeneratePresignedUrlResponse(uploadUrl, fileMetadata.storedPath, fileMetadata.contextKey)
    }

    fun markFilesAsSuccess(files: List<String>) {
        fileStore.markFilesAsSuccess(UploadUrl.of(files))
    }
}
