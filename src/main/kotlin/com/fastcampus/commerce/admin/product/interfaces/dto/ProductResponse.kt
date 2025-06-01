package com.fastcampus.commerce.admin.product.interfaces.dto

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val thumbnail: String,
    val detailImage: String,
    val intensityId: Long,
    val cupSizeId: Long,
    val sellingStatusCode: String,
)
