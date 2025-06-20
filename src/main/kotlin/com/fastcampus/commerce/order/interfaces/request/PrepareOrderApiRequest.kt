package com.fastcampus.commerce.order.interfaces.request

import jakarta.validation.constraints.NotEmpty

data class PrepareOrderApiRequest(
    @field:NotEmpty(message = "주문할 아이템이 누락되었습니다.")
    val cartItemIds: Set<Long>,
)
