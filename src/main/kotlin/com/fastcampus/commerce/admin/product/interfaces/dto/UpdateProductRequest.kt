package com.fastcampus.commerce.admin.product.interfaces.dto

data class UpdateProductRequest(
    val name: String,
    val price: Int,
    val quantity: Int,
    val detailImage: String,
    val thumbnail: String,
    val intensityId: Long,
    val cupSizeId: Long,
)
