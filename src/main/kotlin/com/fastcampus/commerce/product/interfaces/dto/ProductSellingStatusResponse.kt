package com.fastcampus.commerce.product.interfaces.dto

import com.fastcampus.commerce.common.response.EnumResponse

data class ProductSellingStatusResponse(
    val status: List<EnumResponse>,
)
