package com.fastcampus.commerce.file.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import com.fastcampus.commerce.file.domain.model.FileStatus
import java.util.UUID
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "file_metadata")
@Entity
class FileMetadata(
    @Column(name = "storage_context_key_id", nullable = false)
    val contextKey: UUID,
    @Column(nullable = false)
    val storedPath: String,
    @Column(nullable = false)
    val storedFileName: String,
    @Column(nullable = false)
    val originalFileName: String,
    @Column(nullable = false)
    val contentType: String,
    @Column(nullable = false)
    val fileSize: Int,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: FileStatus = FileStatus.PENDING,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun success() {
        this.status = FileStatus.SUCCESS
    }
}
