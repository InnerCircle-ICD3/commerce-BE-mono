package com.fastcampus.commerce.product.domain.repository

import com.fastcampus.commerce.product.domain.entity.ProductCategory
import com.fastcampus.commerce.product.domain.model.CategoryInfo

interface ProductCategoryRepository {
    fun saveAll(productCategories: List<ProductCategory>): List<ProductCategory>

    fun getAllByProductId(productId: Long): List<ProductCategory>

    fun deleteAll(productCategories: List<ProductCategory>)

    fun getCategoryInfosIn(productIds: List<Long>): List<CategoryInfo>
}
