package com.fastcampus.commerce.admin.order.infrastructure.request

import com.fastcampus.commerce.order.domain.entity.OrderStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class AdminOrderSearchRequest(
    val orderNumber: String? = null,
    val nickname: String? = null,
    val productName: String? = null,
    val status: OrderStatus? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null,
) {
    val from = dateFrom?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy.MM.dd")) }
    val to = dateTo?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy.MM.dd")) }
}
