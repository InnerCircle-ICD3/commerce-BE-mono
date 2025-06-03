package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.ProductCategory
import com.fastcampus.commerce.product.domain.error.ProductErrorCode
import com.fastcampus.commerce.product.domain.repository.ProductCategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryStore(
    private val categoryReader: CategoryReader,
    private val productCategoryRepository: ProductCategoryRepository,
) {
    fun mappingProductCategories(productId: Long, categoryIds: List<Long>): List<ProductCategory> {
        val existsCount = categoryReader.countCategoriesByIds(categoryIds)
        if (existsCount != categoryIds.size.toLong()) {
            throw CoreException(ProductErrorCode.INVALID_CATEGORY)
        }
        val productCategories = categoryIds.map { ProductCategory(productId = productId, categoryId = it) }
        return productCategoryRepository.saveAll(productCategories)
    }
}
