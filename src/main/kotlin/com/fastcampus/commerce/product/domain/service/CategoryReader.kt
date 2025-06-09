package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.product.domain.entity.ProductCategory
import com.fastcampus.commerce.product.domain.model.CategoryDetail
import com.fastcampus.commerce.product.domain.model.CategoryType
import com.fastcampus.commerce.product.domain.model.ProductCategoryInfo
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

    fun getProductCategoryMap(productIds: List<Long>): Map<Long, ProductCategoryInfo> {
        return productCategoryRepository.getCategoryInfosIn(productIds)
            .groupBy { it.productId }
            .mapValues { (_, categoryInfos) ->
                ProductCategoryInfo(
                    intensity = categoryInfos.find { it.groupTitle == CategoryType.INTENSITY.groupTitle }?.categoryName ?: "",
                    cupSize = categoryInfos.find { it.groupTitle == CategoryType.CUP_SIZE.groupTitle }?.categoryName ?: "",
                )
            }
    }

    fun getProductCategory(productId: Long): ProductCategoryInfo {
        return getProductCategoryMap(listOf(productId))[productId] ?: ProductCategoryInfo.empty()
    }

    fun getCategoryDetail(): List<CategoryDetail> {
        return categoryRepository.getCategoryDetail()
    }
}
