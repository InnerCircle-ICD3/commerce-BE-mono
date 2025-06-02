package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.domain.entity.FileMetadata
import com.fastcampus.commerce.file.domain.entity.StorageContextKey
import com.fastcampus.commerce.file.domain.repository.FileMetadataRepository
import com.fastcampus.commerce.file.domain.repository.StorageContextKeyRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FileStore(
    private val storageContextKeyRepository: StorageContextKeyRepository,
    private val fileMetadataRepository: FileMetadataRepository,
    private val filePathGenerator: FilePathGenerator,
    private val fileReader: FileReader,
) {
    @Transactional(readOnly = false)
    fun prepareGeneratePresignedUrl(uploaderId: Long, request: GeneratePresignedUrlRequest): FileMetadata {
        val storageContextKey = if (request.contextId != null) {
            fileReader.getStorageContextKey(request.contextId, uploaderId)
        } else {
            storageContextKeyRepository.save(StorageContextKey(uploaderId))
        }

        val filePath = filePathGenerator.generate(storageContextKey.id.toString(), request)

        return fileMetadataRepository.save(
            FileMetadata(
                contextKey = storageContextKey.id,
                storedPath = filePath.path,
                storedFileName = filePath.fileName,
                originalFileName = request.fileName,
                contentType = request.contentType,
                fileSize = request.fileSize,
            ),
        )
    }
}
