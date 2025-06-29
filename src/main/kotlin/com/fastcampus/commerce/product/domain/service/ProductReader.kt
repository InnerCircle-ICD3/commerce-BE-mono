package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.error.ProductErrorCode
import com.fastcampus.commerce.product.domain.model.ProductInfo
import com.fastcampus.commerce.product.domain.model.SearchAdminProductCondition
import com.fastcampus.commerce.product.domain.model.SearchProductCondition
import com.fastcampus.commerce.product.domain.repository.InventoryRepository
import com.fastcampus.commerce.product.domain.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ProductReader(
    private val productRepository: ProductRepository,
    private val inventoryRepository: InventoryRepository,
) {
    fun getInventoryByProductId(productId: Long): Inventory {
        return inventoryRepository.findByProductId(productId)
            .orElseThrow { CoreException(ProductErrorCode.INVENTORY_NOT_FOUND) }
    }

    fun getInventoryByProductIdForUpdate(productId: Long): Inventory {
        return inventoryRepository.findByProductIdForUpdate(productId)
            .orElseThrow { CoreException(ProductErrorCode.INVENTORY_NOT_FOUND) }
    }

    fun getProductById(productId: Long): Product {
        return productRepository.findById(productId)
            .orElseThrow { CoreException(ProductErrorCode.PRODUCT_NOT_FOUND) }
    }

    fun searchProducts(condition: SearchProductCondition, pageable: Pageable): Page<ProductInfo> {
        return productRepository.searchProducts(condition, pageable)
    }

    fun getProductInfo(productId: Long): ProductInfo {
        val product = getProductById(productId)
        val inventory = getInventoryByProductId(productId)
        return ProductInfo.of(product, inventory)
    }

    fun searchProductsForAdmin(condition: SearchAdminProductCondition, pageable: Pageable): Page<ProductInfo> {
        return productRepository.searchProductsForAdmin(condition, pageable)
    }

    fun findLatestProducts(limit: Int): List<ProductInfo> {
        return productRepository.findLatestProducts(limit)
    }

    fun findBestProducts(baseDate: LocalDateTime, limit: Int): List<ProductInfo> {
        return productRepository.findBestProducts(baseDate, limit)
    }
}
