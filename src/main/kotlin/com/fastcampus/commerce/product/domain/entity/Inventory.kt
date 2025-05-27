package com.fastcampus.commerce.product.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "inventory")
@Entity
class Inventory(
    @Column(name = "product_id", nullable = false)
    val productId: Long,
    @Column(nullable = false)
    var quantity: Int,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime

    fun updateQuantity(quantity: Int) {
        this.quantity = quantity
    }
}
