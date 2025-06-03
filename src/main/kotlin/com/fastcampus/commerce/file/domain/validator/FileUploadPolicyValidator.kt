package com.fastcampus.commerce.file.domain.validator

import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import org.springframework.stereotype.Component

@Component
class FileUploadPolicyValidator(
    private val fileNameValidator: FileNameValidator,
    private val filePolicyValidator: FilePolicyValidator,
) {
    fun validate(request: GeneratePresignedUrlRequest) {
        fileNameValidator.validate(request.fileName)
        filePolicyValidator.validate(request)
    }
}
