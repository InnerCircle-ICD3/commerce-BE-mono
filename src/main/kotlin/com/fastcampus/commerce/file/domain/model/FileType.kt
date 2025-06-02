package com.fastcampus.commerce.file.domain.model

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import java.util.Locale

enum class FileType(
    private val mimeTypes: Set<String>,
) {
    IMAGE(setOf("IMAGE/JPEG", "IMAGE/PNG")),
    VIDEO(setOf("VIDEO/MP4")),
    ;

    companion object {
        fun from(value: String): FileType {
            val upper = value.uppercase(Locale.getDefault())
            return entries.firstOrNull { it.mimeTypes.contains(upper) }
                ?: throw CoreException(FileErrorCode.INVALID_FILE_TYPE)
        }
    }
}
