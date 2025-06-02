package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.entity.StorageContextKey
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.repository.StorageContextKeyRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FileReader(
    private val storageContextKeyRepository: StorageContextKeyRepository,
) {
    fun getStorageContextKey(storageContextKey: String, adminId: Long): StorageContextKey {
        val id = try {
            UUID.fromString(storageContextKey)
        } catch (e: IllegalArgumentException) {
            throw CoreException(FileErrorCode.INVALID_STORAGE_CONTEXT_KEY)
        }
        return storageContextKeyRepository.findByIdAndAdminId(id, adminId)
            .orElseThrow { throw CoreException(FileErrorCode.INVALID_STORAGE_CONTEXT_KEY) }
    }
}
