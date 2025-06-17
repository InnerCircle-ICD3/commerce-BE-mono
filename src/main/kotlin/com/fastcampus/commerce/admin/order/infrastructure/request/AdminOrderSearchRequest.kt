package com.fastcampus.commerce.admin.order.infrastructure.request

import java.time.LocalDate

data class AdminOrderSearchRequest(
    val keyword: String? = null, // 주문번호, 고객명, 상품명 등 검색
    val status: String? = null, // 주문 상태 필터링
    val dateFrom: LocalDate? = null, // 시작일
    val dateTo: LocalDate? = null, // 종료일
)
