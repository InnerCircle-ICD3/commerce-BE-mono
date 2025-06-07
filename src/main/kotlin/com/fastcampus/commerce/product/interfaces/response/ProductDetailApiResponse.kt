package com.fastcampus.commerce.product.interfaces.response

import com.fastcampus.commerce.product.application.response.ProductDetailResponse

data class ProductDetailApiResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val thumbnail: String,
    val detailImage: String,
    val intensity: String,
    val cupSize: String,
) {
    companion object {
        fun from(product: ProductDetailResponse) =
            ProductDetailApiResponse(
                id = product.id,
                name = product.name,
                price = product.price,
                quantity = product.quantity,
                thumbnail = product.thumbnail,
                detailImage = product.detailImage,
                intensity = product.intensity,
                cupSize = product.cupSize,
            )
    }
}
