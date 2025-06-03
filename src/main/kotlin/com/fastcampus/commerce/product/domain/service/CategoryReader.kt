package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.product.domain.entity.ProductCategory
import com.fastcampus.commerce.product.domain.repository.CategoryRepository
import com.fastcampus.commerce.product.domain.repository.ProductCategoryRepository
import org.springframework.stereotype.Component

@Component
class CategoryReader(
    private val categoryRepository: CategoryRepository,
    private val productCategoryRepository: ProductCategoryRepository,
) {
    fun countCategoriesByIds(categoryIds: List<Long>): Long {
        return categoryRepository.countCategoriesByIds(categoryIds)
    }

    fun getAllProductCategoriesByProductId(productId: Long): List<ProductCategory> {
        return productCategoryRepository.getAllByProductId(productId)
    }
}
