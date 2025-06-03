package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.ProductCategory
import com.fastcampus.commerce.product.domain.error.ProductErrorCode
import com.fastcampus.commerce.product.domain.repository.ProductCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryStore(
    private val categoryReader: CategoryReader,
    private val productCategoryRepository: ProductCategoryRepository,
) {
    @Transactional(readOnly = false)
    fun mappingProductCategories(productId: Long, categoryIds: List<Long>): List<ProductCategory> {
        val existsCount = categoryReader.countCategoriesByIds(categoryIds)
        if (existsCount != categoryIds.size.toLong()) {
            throw CoreException(ProductErrorCode.INVALID_CATEGORY)
        }
        val productCategories = categoryIds.map { ProductCategory(productId = productId, categoryId = it) }
        return productCategoryRepository.saveAll(productCategories)
    }

    @Transactional(readOnly = false)
    fun removeProductCategories(productId: Long) {
        val productCategories: List<ProductCategory> = categoryReader.getAllProductCategoriesByProductId(productId)
        productCategoryRepository.deleteAll(productCategories)
    }
}
