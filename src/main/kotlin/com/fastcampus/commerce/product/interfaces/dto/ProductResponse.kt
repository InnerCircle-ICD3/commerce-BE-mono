package com.fastcampus.commerce.product.interfaces.dto

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val thumbnail: String,
    val detailImage: String,
    val intensity: String,
    val cupSize: String,
    val isSoldOut: Boolean,
)
