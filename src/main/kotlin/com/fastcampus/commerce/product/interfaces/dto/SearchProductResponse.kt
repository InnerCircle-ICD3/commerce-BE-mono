package com.fastcampus.commerce.product.interfaces.dto

data class SearchProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val thumbnail: String,
    val intensity: String,
    val cupSize: String,
    val isSoldOut: Boolean,
)
