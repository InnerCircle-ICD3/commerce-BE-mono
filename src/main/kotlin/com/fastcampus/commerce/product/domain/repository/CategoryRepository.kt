package com.fastcampus.commerce.product.domain.repository

import com.fastcampus.commerce.product.domain.model.CategoryDetail

interface CategoryRepository {
    fun countCategoriesByIds(categoryIds: List<Long>): Long

    fun getCategoryDetail(): List<CategoryDetail>
}
