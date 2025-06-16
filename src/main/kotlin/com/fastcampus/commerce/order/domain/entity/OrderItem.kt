package com.fastcampus.commerce.order.domain.entity

import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table

@SQLDelete(sql = "update order_items set deleted_at = now() where id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "order_items")
@Entity
class OrderItem(
    @Column(name = "order_id", nullable = false)
    var orderId: Long,
    @JoinColumn(name = "product_snapshot_id", nullable = false)
    val productSnapshotId: Long,
    @Column(nullable = false)
    val quantity: Int,
    @Column(nullable = false)
    val unitPrice: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var deletedAt: LocalDateTime? = null

    fun totalPrice(): Int = quantity * unitPrice
}
