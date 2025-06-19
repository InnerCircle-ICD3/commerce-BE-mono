package com.fastcampus.commerce.admin.order.infrastructure.request

data class AdminOrderSearchRequest(
    val keyword: String? = null, // 주문번호, 고객명, 상품명 등 검색
    val status: String? = null, // 주문 상태 필터링
    val dateFrom: String? = null, // 시작일
    val dateTo: String? = null, // 종료일
)
