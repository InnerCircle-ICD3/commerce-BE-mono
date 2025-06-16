package com.fastcampus.commerce.product.application

import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.product.application.response.SearchProductResponse
import com.fastcampus.commerce.product.domain.model.ProductCategoryInfo
import com.fastcampus.commerce.product.domain.service.CategoryReader
import com.fastcampus.commerce.product.domain.service.ProductReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MainProductService(
    private val timeProvider: TimeProvider,
    private val productReader: ProductReader,
    private val categoryReader: CategoryReader,
) {
    @Transactional(readOnly = true)
    fun getNewProducts(): List<SearchProductResponse> {
        val limit = 5
        val productInfos = productReader.findLatestProducts(limit)
        val productIds = productInfos.map { it.id }
        val categoryMap = categoryReader.getProductCategoryMap(productIds)
        return productInfos.map { productInfo ->
            val productCategoryInfo = categoryMap[productInfo.id] ?: ProductCategoryInfo.empty()
            SearchProductResponse.of(productInfo, productCategoryInfo)
        }
    }

    @Transactional(readOnly = true)
    fun getBestProducts(): List<SearchProductResponse> {
        val limit = 5
        val oneWeekAgo = timeProvider.now().minusDays(7)
        val productInfos = productReader.findBestProducts(oneWeekAgo, limit)
        val productIds = productInfos.map { it.id }
        val categoryMap = categoryReader.getProductCategoryMap(productIds)
        return productInfos.map { productInfo ->
            val productCategoryInfo = categoryMap[productInfo.id] ?: ProductCategoryInfo.empty()
            SearchProductResponse.of(productInfo, productCategoryInfo)
        }
    }
}
