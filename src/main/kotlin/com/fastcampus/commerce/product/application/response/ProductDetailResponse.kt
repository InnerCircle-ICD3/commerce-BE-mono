package com.fastcampus.commerce.product.application.response

import com.fastcampus.commerce.product.domain.model.ProductCategoryInfo
import com.fastcampus.commerce.product.domain.model.ProductInfo

data class ProductDetailResponse(
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
        fun of(productInfo: ProductInfo, productCategoryInfo: ProductCategoryInfo): ProductDetailResponse {
            return ProductDetailResponse(
                id = productInfo.id,
                name = productInfo.name,
                price = productInfo.price,
                quantity = productInfo.quantity,
                thumbnail = productInfo.thumbnail,
                detailImage = productInfo.detailImage,
                intensity = productCategoryInfo.intensity,
                cupSize = productCategoryInfo.cupSize,
            )
        }
    }
}
