package com.fastcampus.commerce.file.interfaces

import com.fastcampus.commerce.file.application.FileService
import com.fastcampus.commerce.file.interfaces.request.GeneratePresignedUrlApiRequest
import com.fastcampus.commerce.file.interfaces.response.GeneratePresignedUrlApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RequestMapping("/files")
@RestController
class FileController(
    private val fileService: FileService,
) {
    @PostMapping("/presigned-url")
    fun generatePresignedUrl(
        @Valid @RequestBody request: GeneratePresignedUrlApiRequest,
    ): GeneratePresignedUrlApiResponse {
        return GeneratePresignedUrlApiResponse.from(fileService.generatePresignedUrl(1L, request.toServiceRequest()))
    }
}
