package com.fastcampus.commerce.product.domain.service.dto

import com.fastcampus.commerce.product.domain.entity.Product

data class ProductRegister(
    val name: String,
    val price: Int,
    val quantity: Int,
    val detailImage: String,
    val thumbnail: String,
    val categoryIds: List<Long>,
    val registerId: Long,
) {
    fun toProduct(): Product =
        Product(
            name = name,
            price = price,
            thumbnail = thumbnail,
            detailImage = detailImage,
        )
}
