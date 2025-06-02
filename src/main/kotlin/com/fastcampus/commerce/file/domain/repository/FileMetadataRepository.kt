package com.fastcampus.commerce.file.domain.repository

import com.fastcampus.commerce.file.domain.entity.FileMetadata
import org.springframework.data.jpa.repository.JpaRepository

interface FileMetadataRepository : JpaRepository<FileMetadata, Long>
