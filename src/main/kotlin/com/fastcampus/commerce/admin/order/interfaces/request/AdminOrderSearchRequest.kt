package com.fastcampus.commerce.admin.order.interfaces.request

import com.fastcampus.commerce.order.domain.entity.OrderStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import jakarta.validation.constraints.Pattern

data class AdminOrderSearchRequest(
    val orderNumber: String? = null,
    val nickname: String? = null,
    val productName: String? = null,
    val status: OrderStatus? = null,
    @field:Pattern(regexp = "^\\d{4}\\.\\d{2}\\.\\d{2}", message = "날짜는 yyyy.MM.dd 형식으로 입력해주세요.")
    val dateFrom: String? = null,
    @field:Pattern(regexp = "^\\d{4}\\.\\d{2}\\.\\d{2}", message = "날짜는 yyyy.MM.dd 형식으로 입력해주세요.")
    val dateTo: String? = null,
) {
    val from get() = dateFrom?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy.MM.dd")) }
    val to get() = dateTo?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy.MM.dd")) }
}
