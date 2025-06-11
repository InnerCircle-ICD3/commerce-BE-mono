package com.fastcampus.commerce.user.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@SQLDelete(sql = "update oauth2_providers set deleted_at = now() where id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "oauth2_providers")
@Entity
class Oauth2Provider(
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val isActive: Boolean = true,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var deletedAt: LocalDateTime? = null
}
