package com.fastcampus.commerce.product.domain.repository

import com.fastcampus.commerce.product.domain.entity.ProductCategory

interface ProductCategoryRepository {
    fun saveAll(productCategories: List<ProductCategory>): List<ProductCategory>
}
