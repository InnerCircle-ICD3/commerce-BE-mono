package com.fastcampus.commerce.product.domain.repository

interface CategoryRepository {
    fun countCategoriesByIds(categoryIds: List<Long>): Long
}
