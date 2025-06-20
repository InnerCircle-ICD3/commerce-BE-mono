package com.fastcampus.commerce.file.interfaces.request

import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.domain.model.DomainType
import com.fastcampus.commerce.file.domain.model.FileType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class GeneratePresignedUrlApiRequest(
    @field:NotBlank(message = "파일명이 비어있습니다.")
    val fileName: String? = null,
    @field:NotBlank(message = "파일의 Content-Type이 비어있습니다.")
    val contentType: String? = null,
    @field:Positive(message = "파일 크기가 올바르지 않습니다.")
    val fileSize: Int,
    @field:NotBlank(message = "기준 도메인이 비어있습니다.")
    val domainType: String? = null,
    @field:NotBlank(message = "도메인 컨텍스트가 비어있습니다.")
    val domainContext: String? = null,
    val contextId: String? = null,
) {
    fun toServiceRequest() =
        GeneratePresignedUrlRequest(
            fileName = fileName!!,
            contentType = contentType!!,
            fileSize = fileSize,
            domainType = DomainType.from(domainType!!),
            domainContext = domainContext!!,
            fileType = FileType.from(contentType),
            contextId = contextId,
        )
}
