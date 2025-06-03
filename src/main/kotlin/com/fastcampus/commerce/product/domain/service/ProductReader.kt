package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.error.ProductErrorCode
import com.fastcampus.commerce.product.domain.repository.InventoryRepository
import org.springframework.stereotype.Service

@Service
class ProductReader (
    private val inventoryRepository: InventoryRepository,
){
    fun getInventoryByProductId(productId: Long): Inventory {
        return inventoryRepository.findByProductId(productId)
        .orElseThrow { CoreException(ProductErrorCode.INVENTORY_NOT_FOUND) }
    }
}
