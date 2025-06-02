package com.fastcampus.commerce.file.interfaces.response

import com.fastcampus.commerce.file.application.response.GeneratePresignedUrlResponse
import java.util.UUID

data class GeneratePresignedUrlApiResponse(
    val uploadUrl: String,
    val key: String,
    val contextId: UUID,
) {
    companion object {
        fun from(response: GeneratePresignedUrlResponse) =
            GeneratePresignedUrlApiResponse(
                uploadUrl = response.uploadUrl,
                key = response.key,
                contextId = response.contextId,
            )
    }
}
