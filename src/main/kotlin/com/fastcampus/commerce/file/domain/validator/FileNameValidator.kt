package com.fastcampus.commerce.file.domain.validator

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import org.springframework.stereotype.Component

@Component
class FileNameValidator {
    fun validate(fileName: String) {
        if (fileName.isBlank()) {
            throw CoreException(FileErrorCode.FILE_NAME_EMPTY)
        }
        if (fileName.contains("..")) {
            throw CoreException(FileErrorCode.INVALID_FILE_NAME)
        }
        if (fileName.length > MAX_FILE_NAME_LENGTH) {
            throw CoreException(FileErrorCode.FILE_NAME_TOO_LONG)
        }
    }

    companion object {
        const val MAX_FILE_NAME_LENGTH = 255
    }
}
