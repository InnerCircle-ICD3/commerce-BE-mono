package com.fastcampus.commerce.product.domain.repository

import com.fastcampus.commerce.product.domain.entity.Inventory

interface InventoryRepository {
    fun save(inventory: Inventory): Inventory
}
