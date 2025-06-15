package com.fastcampus.commerce.payment.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@SQLDelete(sql = "update payments set deleted_at = now() where id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "payments")
@Entity
class Payment(
    @Column(nullable = false)
    val paymentNumber: String,
    @Column(name = "order_id", nullable = false)
    val orderId: Long,
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(nullable = false)
    val amount: Int,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val paymentMethod: PaymentMethod,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PaymentStatus = PaymentStatus.WAITING,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var transactionId: String? = null

    @Column
    var failedReason: String? = null

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime

    @Column
    var deletedAt: LocalDateTime? = null

    fun paid(now: LocalDateTime) {
        if (this.status == PaymentStatus.WAITING) {
            status = PaymentStatus.COMPLETED
            updatedAt = now
        }
    }

    fun cancel(cancelledAt: LocalDateTime) {
        if (this.status == PaymentStatus.COMPLETED || this.status == PaymentStatus.WAITING) {
            status = PaymentStatus.CANCELLED
            updatedAt = cancelledAt
        }
    }

    fun refundApprove(refundAt: LocalDateTime) {
        if (this.status == PaymentStatus.COMPLETED || this.status == PaymentStatus.WAITING) {
            status = PaymentStatus.REFUNDED
            updatedAt = refundAt
        }
    }
}
