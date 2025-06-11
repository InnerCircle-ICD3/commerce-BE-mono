package com.fastcampus.commerce.order.interfaces.response

data class SearchOrderApiResponse(
    val orderNumber: String,
    val orderName: String,
    val orderStatus: String,
    val finalTotalPrice: Int,
    val cancellable: Boolean,
    val refundable: Boolean,
)
