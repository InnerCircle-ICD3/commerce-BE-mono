package com.fastcampus.commerce.admin.product.application.request

import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.product.domain.model.ProductUpdater

data class UpdateProductRequest(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val thumbnail: String,
    val detailImage: String,
    val intensityId: Long,
    val cupSizeId: Long,
    val status: SellingStatus,
) {
    val categoryIds get() = listOf(intensityId, cupSizeId)
    val files get() = listOf(detailImage, thumbnail)

    fun toCommand(registerId: Long): ProductUpdater =
        ProductUpdater(
            id = id,
            name = name,
            price = price,
            quantity = quantity,
            thumbnail = thumbnail,
            detailImage = detailImage,
            categoryIds = categoryIds,
            status = status,
            updaterId = registerId,
        )
}
