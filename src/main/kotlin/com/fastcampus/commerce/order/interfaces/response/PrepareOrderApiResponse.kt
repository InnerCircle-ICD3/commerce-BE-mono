package com.fastcampus.commerce.order.interfaces.response

import com.fastcampus.commerce.common.response.EnumResponse

data class PrepareOrderApiResponse(
    val cartItemIds: List<Long>,
    val itemsSubtotal: Int,
    val shippingFee: Int,
    val finalTotalPrice: Int,
    val items: List<PrepareOrderItemApiResponse>,
    val shippingInfo: PrepareOrderShippingInfoApiResponse? = null,
    val paymentMethod: List<EnumResponse>,
)

data class PrepareOrderItemApiResponse(
    val productId: Long,
    val name: String,
    val thumbnail: String,
    val unitPrice: Int,
    val quantity: Int,
    val itemSubtotal: Int,
)

data class PrepareOrderShippingInfoApiResponse(
    val recipientName: String,
    val recipientPhone: String,
    val zipCode: String,
    val address1: String,
    val address2: String? = null,
)
