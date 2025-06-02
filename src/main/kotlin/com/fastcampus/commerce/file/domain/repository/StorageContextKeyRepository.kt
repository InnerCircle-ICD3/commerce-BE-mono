package com.fastcampus.commerce.file.domain.repository

import com.fastcampus.commerce.file.domain.entity.StorageContextKey
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface StorageContextKeyRepository : JpaRepository<StorageContextKey, UUID> {
    fun findByIdAndAdminId(id: UUID, adminId: Long): Optional<StorageContextKey>
}
