package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.product.domain.repository.CategoryRepository
import org.springframework.stereotype.Component

@Component
class CategoryReader(
    private val categoryRepository: CategoryRepository,
) {
    fun countCategoriesByIds(categoryIds: List<Long>): Long {
        return categoryRepository.countCategoriesByIds(categoryIds)
    }
}
