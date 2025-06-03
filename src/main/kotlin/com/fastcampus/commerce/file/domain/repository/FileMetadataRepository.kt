package com.fastcampus.commerce.file.domain.repository

import com.fastcampus.commerce.file.domain.entity.FileMetadata
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface FileMetadataRepository : JpaRepository<FileMetadata, Long> {
    fun findByStoredPath(storedPath: String): Optional<FileMetadata>
}
