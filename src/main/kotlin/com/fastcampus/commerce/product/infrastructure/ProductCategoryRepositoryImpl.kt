package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.ProductCategory
import com.fastcampus.commerce.product.domain.repository.ProductCategoryRepository
import org.springframework.stereotype.Repository

@Repository
class ProductCategoryRepositoryImpl(
    private val productCategoryJpaRepository: ProductCategoryJpaRepository,
) : ProductCategoryRepository {
    override fun saveAll(productCategories: List<ProductCategory>): List<ProductCategory> {
        return productCategoryJpaRepository.saveAll(productCategories)
    }
}
