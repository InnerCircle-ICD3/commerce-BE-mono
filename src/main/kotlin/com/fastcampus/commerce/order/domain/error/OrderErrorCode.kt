package com.fastcampus.commerce.order.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class OrderErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    ORDER_NOT_FOUND("ORD-001", "주문 내역을 찾을 수 없습니다.", LogLevel.WARN),
    CART_ITEM_NOT_MATCH("ORD-002", "장바구니 항목을 찾을 수 없습니다.", LogLevel.WARN),
    PRODUCT_SNAPSHOT_NOT_FOUND("ORD-003", "주문 상품을 찾을 수 없습니다.", LogLevel.WARN),
    ORDER_QUANTITY_NOT_ENOUGH("ORD-022", "재고가 부족해 주문을 생성할 수 없습니다.", LogLevel.WARN),
    ORDER_DATA_FOR_REVIEW_NOT_FOUND("ORD-501", "리뷰 작성을 위한 주문데이터를 찾을 수 없습니다.", LogLevel.WARN),

    CANNOT_CANCEL("ORD-101", "결제 대기중, 결제 완료 상태인 주문만 취소할 수 있습니다.", LogLevel.WARN),
    UNAUTHORIZED_ORDER_CANCEL("ORD-102", "다른 사람의 결제를 취소할 수 없습니다.", LogLevel.WARN),
    CANNOT_REFUND("ORD-103", "배송중, 배송 완료 상태인 주문만 환불할 수 있습니다.", LogLevel.WARN),
    UNAUTHORIZED_ORDER_REFUND("ORD-104", "다른 사람의 결제를 환불할 수 없습니다.", LogLevel.WARN),
    NOT_REFUND_REQUESTED_APPROVE("ORD-104", "환불요청한 주문만 환불가능합니다.", LogLevel.WARN),
    NOT_REFUND_REQUESTED_REJECT("ORD-105", "환불요청한 주문만 환불거절 가능합니다.", LogLevel.WARN),
    ORDER_CANNOT_BE_CANCELLED("ORD-106", "해당 주문은 관리자에 의해 취소할 수 없는 상태입니다.", LogLevel.WARN),
    CANNOT_PREPARING_SHIPMENT("ORD-107", "결제완료 상태인 주문만 배송준비중으로 변경할 수 있습니다.", LogLevel.WARN),
    CANNOT_SHIPPED("ORD-108", "배송준비중 상태인 주문만 배송중으로 변경할 수 있습니다.", LogLevel.WARN),
    CANNOT_DELIVERED("ORD-109", "배송중 상태인 주문만 배송완료로 변경할 수 있습니다.", LogLevel.WARN),
}
