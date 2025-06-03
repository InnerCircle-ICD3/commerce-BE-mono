package com.fastcampus.commerce.admin.product.application.request

import com.fastcampus.commerce.product.domain.model.ProductRegister

data class RegisterProductRequest(
    val name: String,
    val price: Int,
    val quantity: Int,
    val thumbnail: String,
    val detailImage: String,
    val intensityId: Long,
    val cupSizeId: Long,
) {
    val categoryIds get() = listOf(intensityId, cupSizeId)
    val files get() = listOf(detailImage, thumbnail)

    fun toCommand(registerId: Long): ProductRegister =
        ProductRegister(
            name = name,
            price = price,
            quantity = quantity,
            thumbnail = thumbnail,
            detailImage = detailImage,
            categoryIds = categoryIds,
            registerId = registerId,
        )
}
