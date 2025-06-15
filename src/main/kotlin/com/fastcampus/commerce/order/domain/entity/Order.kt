package com.fastcampus.commerce.order.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@SQLDelete(sql = "update orders set is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "orders")
@Entity
class Order(
    @Column(nullable = false, unique = true)
    val orderNumber: String,
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(nullable = false)
    val totalAmount: Int,
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
    @Column
    var deliveryMessage: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.WAITING_FOR_PAYMENT,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var paidAt: LocalDateTime? = null
    var shippedAt: LocalDateTime? = null
    var deliveredAt: LocalDateTime? = null
    var cancelledAt: LocalDateTime? = null
    var refundRequestedAt: LocalDateTime? = null
    var refundedAt: LocalDateTime? = null
    var refundRejectedAt: LocalDateTime? = null

    var isDeleted: Boolean = false
    var deletedAt: LocalDateTime? = null

    fun paid(paidAt: LocalDateTime) {
        if (this.status == OrderStatus.WAITING_FOR_PAYMENT) {
            this.paidAt = paidAt
            this.status = OrderStatus.PAID
        }
    }

    fun verifyCancelable(requestUserId: Long) {
        if (this.status != OrderStatus.PAID && this.status != OrderStatus.WAITING_FOR_PAYMENT) {
            throw CoreException(OrderErrorCode.CANNOT_CANCEL)
        }
        if (this.userId != requestUserId) {
            throw CoreException(OrderErrorCode.UNAUTHORIZED_ORDER_CANCEL)
        }
    }

    fun cancel(cancelledAt: LocalDateTime) {
        if (this.status == OrderStatus.PAID || this.status == OrderStatus.WAITING_FOR_PAYMENT) {
            this.cancelledAt = cancelledAt
            this.status = OrderStatus.CANCELLED
        }
    }

    fun verifyRefundable(requestUserId: Long) {
        if (this.status != OrderStatus.SHIPPED && this.status != OrderStatus.DELIVERED) {
            throw CoreException(OrderErrorCode.CANNOT_REFUND)
        }
        if (this.userId != requestUserId) {
            throw CoreException(OrderErrorCode.UNAUTHORIZED_ORDER_REFUND)
        }
    }

    fun refundRequest(requestuserId: Long, refundRequestedAt: LocalDateTime) {
        verifyRefundable(requestuserId)
        this.refundRequestedAt = refundRequestedAt
        this.status = OrderStatus.REFUND_REQUESTED
    }

    fun refundApprove(refundAt: LocalDateTime) {
        if (this.status != OrderStatus.REFUND_REQUESTED) {
            throw CoreException(OrderErrorCode.NOT_REFUND_REQUESTED_APPROVE)
        }
        this.refundedAt = refundAt
        this.status = OrderStatus.REFUNDED
    }

    fun refundReject(rejectedAt: LocalDateTime) {
        if (this.status != OrderStatus.REFUND_REQUESTED) {
            throw CoreException(OrderErrorCode.NOT_REFUND_REQUESTED_REJECT)
        }
        this.refundRejectedAt = rejectedAt
        this.status = OrderStatus.REFUND_REJECTED
    }
}
