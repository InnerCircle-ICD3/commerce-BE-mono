package com.fastcampus.commerce.user.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import com.fastcampus.commerce.user.domain.model.UserAddressUpdater
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

@SQLDelete(sql = "update user_addresses set deleted_at = now() where id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "user_addresses")
@Entity
class UserAddress(
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(nullable = false)
    var alias: String,
    @Column(nullable = false)
    var recipientName: String,
    @Column(nullable = false)
    var recipientPhone: String,
    @Column(nullable = false)
    var zipCode: String,
    @Column(nullable = false)
    var address1: String,
    @Column
    var address2: String? = null,
    @Column(nullable = false)
    var isDefault: Boolean = false,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime

    @Column
    var deletedAt: LocalDateTime? = null

    fun unsetAsDefault() {
        isDefault = false
    }

    fun update(updater: UserAddressUpdater) {
        alias = updater.alias
        recipientName = updater.recipientName
        recipientPhone = updater.recipientPhone
        zipCode = updater.zipCode
        address1 = updater.address1
        address2 = updater.address2
        isDefault = updater.isDefault
    }
}
