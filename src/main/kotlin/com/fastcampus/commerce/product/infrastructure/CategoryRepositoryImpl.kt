package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.repository.CategoryRepository
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryImpl(
    private val categoryJpaRepository: CategoryJpaRepository,
) : CategoryRepository {
    override fun countCategoriesByIds(categoryIds: List<Long>): Long {
        return categoryJpaRepository.countCategoriesByIdIn(categoryIds)
    }
}
