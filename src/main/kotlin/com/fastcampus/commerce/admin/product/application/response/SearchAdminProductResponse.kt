package com.fastcampus.commerce.admin.product.application.response

import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.product.domain.model.ProductCategoryInfo
import com.fastcampus.commerce.product.domain.model.ProductInfo

data class SearchAdminProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val thumbnail: String,
    val status: SellingStatus,
    val stock: Int,
    val intensity: String,
    val cupSize: String,
) {
    companion object {
        fun of(productInfo: ProductInfo, productCategoryInfo: ProductCategoryInfo): SearchAdminProductResponse {
            return SearchAdminProductResponse(
                id = productInfo.id,
                name = productInfo.name,
                price = productInfo.price,
                thumbnail = productInfo.thumbnail,
                status = productInfo.status,
                stock = productInfo.quantity,
                intensity = productCategoryInfo.intensity,
                cupSize = productCategoryInfo.cupSize,
            )
        }
    }
}
