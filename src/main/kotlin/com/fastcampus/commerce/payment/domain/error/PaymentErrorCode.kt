package com.fastcampus.commerce.payment.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class PaymentErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    PG_RESULT_NOT_FOUND("PAY-001", "PG사 결제정보를 찾을 수 업습니다.", LogLevel.WARN),
    PAYMENT_NOT_FOUND("PAY-002", "결제 정보를 찾을 수 없습니다.", LogLevel.WARN),
    ALREADY_PAID_ORDER("PAY-003", "이미 결제처리한 주문입니다.=", LogLevel.WARN),
    ALREADY_PAID("PAY-004", "이미 결제되었습니다.", LogLevel.WARN),
    PG_RESULT_NOT_MATCH_PAYMENT("PAY-005", "결제금액이 일치하지 않습니다.", LogLevel.WARN),
    TRANSACTION_ID_EMPTY("PAY-006", "PG사 결제 아이디가 누락되었습니다.", LogLevel.WARN),
    INVALID_PAYMENT_METHOD("PAY-007", "유효하지 않은 결제방식입니다.", LogLevel.WARN),
}
