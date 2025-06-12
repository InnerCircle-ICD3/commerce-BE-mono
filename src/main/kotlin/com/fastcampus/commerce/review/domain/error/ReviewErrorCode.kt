package com.fastcampus.commerce.review.domain.error

import com.fastcampus.commerce.common.error.ErrorCode
import com.fastcampus.commerce.common.error.LogLevel

enum class ReviewErrorCode(
    override val code: String,
    override val message: String,
    override val logLevel: LogLevel,
) : ErrorCode {
    CONTENT_EMPTY("RVW-001", "리뷰 내용을 입력해주세요.", LogLevel.WARN),
    INVALID_RATING("RVW-002", "별점은 1~5점 사이로 선택해주세요.", LogLevel.WARN),
    ORDER_NOT_DELIVERED("RVW-003", "배송완료된 주문건에 대해서만 리뷰를 작성할 수 있습니다.", LogLevel.WARN),
    TOO_LATE("RVW-004", "배송완료 후 30일 이내의 주문건에 대해서만 리뷰를 작성 및 수정할 수 있습니다.", LogLevel.WARN),
    ALREADY_WRITE("RVW-005", "리뷰는 주문 당 한번만 작성가능합니다.", LogLevel.WARN),
    REVIEW_NOT_FOUND("RVW-006", "리뷰를 찾을 수 없습니다.", LogLevel.WARN),
    UNAUTHORIZED_REVIEW_UPDATE("RVW-007", "다른 사람의 리뷰를 수정할 수 없습니다", LogLevel.WARN),
    UNAUTHORIZED_REVIEW_DELETE("RVW-008", "다른 사람의 리뷰를 삭제할 수 없습니다", LogLevel.WARN),

    REPLY_EXISTS("RVW-101", "이미 답글이 작성된 리뷰입니다.", LogLevel.WARN),
    REPLY_CONTENT_EMPTY("RVW-102", "리뷰 답글 내용을 입력해주세요.", LogLevel.WARN),
}
