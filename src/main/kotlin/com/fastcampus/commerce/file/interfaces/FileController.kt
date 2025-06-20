package com.fastcampus.commerce.file.interfaces

import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.file.application.FileCommandService
import com.fastcampus.commerce.file.interfaces.request.GeneratePresignedUrlApiRequest
import com.fastcampus.commerce.file.interfaces.response.GeneratePresignedUrlApiResponse
import com.fastcampus.commerce.user.domain.enums.UserRole
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RequestMapping("/files")
@RestController
class FileController(
    private val fileCommandService: FileCommandService,
) {
    @PostMapping("/presigned-url")
    fun generatePresignedUrl(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @Valid @RequestBody request: GeneratePresignedUrlApiRequest,
    ): GeneratePresignedUrlApiResponse {
        return GeneratePresignedUrlApiResponse.from(fileCommandService.generatePresignedUrl(user.id, request.toServiceRequest()))
    }
}
