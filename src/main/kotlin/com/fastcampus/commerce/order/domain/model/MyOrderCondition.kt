package com.fastcampus.commerce.order.domain.model

import com.fastcampus.commerce.order.domain.entity.OrderStatus
import java.time.LocalDate

data class MyOrderCondition (
    val userId: Long,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
    val status: OrderStatus? = null,
)
