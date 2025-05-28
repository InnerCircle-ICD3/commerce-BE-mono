package com.fastcampus.commerce.cart.domain.entity

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

@SQLDelete(sql = "update cart_items set deleted_at = now() where id = ?")
@SQLRestriction("deletedAt is null")
@Table(name = "cart_items")
@Entity
class CartItem(
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(name = "product_id", nullable = false)
    val productId: Long,
    @Column(nullable = false)
    val quantity: Int,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime

    @Column
    var deletedAt: LocalDateTime? = null
}
