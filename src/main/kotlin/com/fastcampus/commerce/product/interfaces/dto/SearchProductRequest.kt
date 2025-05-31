package com.fastcampus.commerce.product.interfaces.dto

data class SearchProductRequest(
    val name: String? = null,
    val intensityId: Long? = null,
    val cupSizeId: Long? = null,
)
