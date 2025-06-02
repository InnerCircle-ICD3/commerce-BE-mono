package com.fastcampus.commerce.file.domain.validator

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.service.FilePolicyProvider
import org.springframework.stereotype.Component

@Component
class FilePolicyValidator(
    private val filePolicyProvider: FilePolicyProvider,
) {
    fun validate(command: GeneratePresignedUrlRequest) {
        val policy = filePolicyProvider.resolve(
            domain = command.domainType,
            context = command.domainContext,
            fileType = command.fileType,
        )

        if (command.fileSize > policy.maxSize) {
            throw CoreException(FileErrorCode.FILE_SIZE_TOO_LARGE)
        }

        val extension = command.fileName.substringAfterLast('.', "").lowercase()
        if (!policy.allowedExtensions.contains(extension)) {
            throw CoreException(FileErrorCode.INVALID_FILE_TYPE)
        }
    }
}
