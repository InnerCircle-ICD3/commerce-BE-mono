package com.fastcampus.commerce.cart.application

import com.fastcampus.commerce.product.domain.entity.Inventory
import org.springframework.stereotype.Service

interface InventoryService {
    fun findInventoryByProductId(productId: Long): Inventory?
}

@Service
class TemporaryInventoryService : InventoryService {
    private val inventoryCache = mutableMapOf<Long, Inventory>()

    override fun findInventoryByProductId(productId: Long): Inventory? {
        return inventoryCache.computeIfAbsent(productId) { pid ->
            Inventory(
                productId = pid,
                quantity = 10,
            )
        }
    }
}
