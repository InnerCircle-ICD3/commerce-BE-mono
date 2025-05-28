package com.fastcampus.commerce.file.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import java.util.UUID
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "storage_context_key")
@Entity
class StorageContextKey(
    @Column(nullable = false)
    val adminId: Long,
) : BaseEntity() {
    @Id
    val id: UUID = UUID.randomUUID()
}
