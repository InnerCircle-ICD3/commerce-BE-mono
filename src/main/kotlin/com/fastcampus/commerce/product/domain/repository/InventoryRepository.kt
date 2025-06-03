package com.fastcampus.commerce.product.domain.repository

import com.fastcampus.commerce.product.domain.entity.Inventory
import java.util.Optional

interface InventoryRepository {
    fun save(inventory: Inventory): Inventory

    fun findByProductId(productId: Long): Optional<Inventory>

    fun findByProductIdForUpdate(productId: Long): Optional<Inventory>

    fun delete(inventory: Inventory)
}
