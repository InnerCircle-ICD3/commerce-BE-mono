package com.fastcampus.commerce.product.interfaces.dto

data class MainProductResponses(
    val best: List<MainProductResponse>,
    val new: List<MainProductResponse>,
)

data class MainProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val thumbnail: String,
    val intensity: String,
    val cupSize: String,
)
