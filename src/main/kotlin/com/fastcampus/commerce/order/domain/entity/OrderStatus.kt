package com.fastcampus.commerce.order.domain.entity

enum class OrderStatus {
    WAITING_FOR_PAYMENT,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUND_REQUESTED,
    REFUNDED,
    REFUND_REJECTED,
    ;

    fun isCancellable(): Boolean {
        return this == PAID || this == SHIPPED || this == DELIVERED || this == WAITING_FOR_PAYMENT
    }
}
