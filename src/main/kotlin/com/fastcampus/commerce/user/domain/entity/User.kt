package com.fastcampus.commerce.user.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@SQLDelete(sql = "update users set is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "users")
@Entity
class User(
    @Column(nullable = false, unique = true)
    val externalId: String,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var email: String,
    @Column(nullable = false)
    var nickname: String,
    /*@Column(nullable = false)
    var profileImage: String,*/
    @Column(nullable = true)
    var profileImage: String? = null
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var isDeleted: Boolean = false

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime

    @Column
    var deletedAt: LocalDateTime? = null
}
