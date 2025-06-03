package com.fastcampus.commerce.file.domain.model

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.entity.FileMetadata
import com.fastcampus.commerce.file.domain.error.FileErrorCode

data class ActualFile(
    val size: Long,
    val contentType: String,
) {
    fun verify(metadata: FileMetadata) {
        if (this.size != metadata.fileSize.toLong()) {
            throw CoreException(FileErrorCode.FILE_NOT_MATCH)
        }
        if (this.contentType != metadata.contentType) {
            throw CoreException(FileErrorCode.FILE_NOT_MATCH)
        }
    }
}
