package com.fastcampus.commerce.order.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class OrderErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    ORDER_NOT_FOUND("ORD-001", "주문 내역을 찾을 수 없습니다.", LogLevel.WARN),
    ORDER_DATA_FOR_REVIEW_NOT_FOUND("ORD-501", "리뷰 작성을 위한 주문데이터를 찾을 수 없습니다.", LogLevel.WARN),

    CANNOT_CANCEL("ORD-101", "결제 대기중, 결제 완료 상태인 주문만 취소할 수 있습니다.", LogLevel.WARN),
    UNAUTHORIZED_ORDER_CANCEL("ORD-102", "다른 사람의 결제를 취소할 수 없습니다.", LogLevel.WARN),
    CANNOT_REFUND("ORD-103", "배송중, 배송 완료 상태인 주문만 환불할 수 있습니다.", LogLevel.WARN),
    UNAUTHORIZED_ORDER_REFUND("ORD-104", "다른 사람의 결제를 환불할 수 없습니다.", LogLevel.WARN),
    NOT_REFUND_REQUESTED_APPROVE("ORD-104", "환불요청한 주문만 환불가능합니다.", LogLevel.WARN),
    NOT_REFUND_REQUESTED_REJECT("ORD-105", "환불요청한 주문만 환불거절 가능합니다.", LogLevel.WARN),
    ORDER_CANNOT_BE_CANCELLED("ORD-106", "해당 주문은 관리자에 의해 취소할 수 없는 상태입니다.", LogLevel.WARN),
}
