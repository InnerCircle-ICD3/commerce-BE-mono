package com.fastcampus.commerce.product.domain.model

import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.querydsl.core.annotations.QueryProjection

data class ProductInfo
    @QueryProjection
    constructor(
        val id: Long,
        val name: String,
        val price: Int,
        val quantity: Int,
        val thumbnail: String,
        val detailImage: String,
        val status: SellingStatus,
    ) {
        companion object {
            fun of(product: Product, inventory: Inventory): ProductInfo {
                return ProductInfo(
                    id = product.id!!,
                    name = product.name,
                    price = product.price,
                    quantity = inventory.quantity,
                    thumbnail = product.thumbnail,
                    detailImage = product.detailImage,
                    status = product.status,
                )
            }
        }
    }

data class ProductCategoryInfo(
    val intensity: String,
    val cupSize: String,
) {
    companion object {
        fun empty(): ProductCategoryInfo {
            return ProductCategoryInfo(
                intensity = "",
                cupSize = "",
            )
        }
    }
}

data class CategoryInfo(
    val productId: Long,
    val groupId: Long,
    val groupTitle: String,
    val categoryId: Long,
    val categoryName: String,
    val sortOrder: Int,
)
