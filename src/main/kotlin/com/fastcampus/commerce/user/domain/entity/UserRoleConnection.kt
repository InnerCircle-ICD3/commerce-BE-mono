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

@SQLDelete(sql = "update user_role_connections set deleted_at = now() where id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "user_role_connections")
@Entity
class UserRoleConnection(
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(name = "role_id", nullable = false)
    val roleId: Long,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var deletedAt: LocalDateTime? = null
}
