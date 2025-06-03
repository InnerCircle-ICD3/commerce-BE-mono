package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.error.ProductErrorCode
import com.fastcampus.commerce.product.domain.repository.InventoryRepository
import com.fastcampus.commerce.product.domain.repository.ProductRepository
import org.springframework.stereotype.Component

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
}
