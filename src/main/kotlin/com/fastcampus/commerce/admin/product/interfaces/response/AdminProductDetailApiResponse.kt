package com.fastcampus.commerce.admin.product.interfaces.response

import com.fastcampus.commerce.admin.product.application.response.AdminProductDetailResponse
import com.fastcampus.commerce.product.domain.entity.SellingStatus

data class AdminProductDetailApiResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val thumbnail: String,
    val detailImage: String,
    val intensity: String,
    val cupSize: String,
    val status: SellingStatus,
) {
    companion object {
        fun from(product: AdminProductDetailResponse) =
            AdminProductDetailApiResponse(
                id = product.id,
                name = product.name,
                price = product.price,
                quantity = product.quantity,
                thumbnail = product.thumbnail,
                detailImage = product.detailImage,
                intensity = product.intensity,
                cupSize = product.cupSize,
                status = product.status,
            )
    }
}
