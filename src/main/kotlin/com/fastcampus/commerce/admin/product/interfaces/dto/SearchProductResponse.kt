package com.fastcampus.commerce.admin.product.interfaces.dto

data class SearchProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val intensity: String,
    val cupSize: String,
    val sellingStatus: String,
)
