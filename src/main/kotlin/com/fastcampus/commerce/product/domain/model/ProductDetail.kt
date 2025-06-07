package com.fastcampus.commerce.product.domain.model

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
    )

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
