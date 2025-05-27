package com.fastcampus.commerce.order.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "product_snapshots")
@Entity
class ProductSnapshot(
    @Column(name = "product_id", nullable = false)
    val productId: Long,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val price: Int,
    @Column(nullable = false)
    val thumbnail: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
