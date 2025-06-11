package com.fastcampus.commerce.order.interfaces.request

data class PrepareOrderApiRequest(
    val cartItemIds: List<Long>,
)
