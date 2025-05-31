package com.fastcampus.commerce.file.interfaces

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.interfaces.dto.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.interfaces.dto.GeneratePresignedUrlResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/files")
@RestController
class FileController {
    @PostMapping("/presigned-url")
    fun getPresignedUrl(
        @RequestBody request: GeneratePresignedUrlRequest,
    ): GeneratePresignedUrlResponse {
        if (request.contentType != "image/png") {
            throw CoreException(FileErrorCode.INVALID_FILE_POLICY)
        }
        return GeneratePresignedUrlResponse(
            uploadUrl = "https://801base.s3.amazonaws.com/product/f1df3364-1b59-4d76-bf69-618caffb4123/thumbnail.jpg",
            key = "product/f1df3364-1b59-4d76-bf69-618caffb4123/thumbnail.jpg",
            contextId = "f1df3364-1b59-4d76-bf69-618caffb4123",
        )
    }
}
