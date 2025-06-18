package com.fastcampus.commerce.order.domain.entity

enum class OrderStatus(
    val label: String
) {
    WAITING_FOR_PAYMENT("결제 대기중"),
    PAID("결제완료"),
    SHIPPED("배송 중"),
    DELIVERED("배송완료"),
    CANCELLED("주문취소"),
    REFUND_REQUESTED("환불 요청"),
    REFUNDED("환불"),
    REFUND_REJECTED("환불 거절"),
    ;

    fun isCancellable(): Boolean {
        return this == PAID || this == SHIPPED || this == DELIVERED || this == WAITING_FOR_PAYMENT
    }
}
