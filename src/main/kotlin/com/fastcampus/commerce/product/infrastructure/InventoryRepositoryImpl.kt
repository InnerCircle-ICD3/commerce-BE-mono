package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.repository.InventoryRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class InventoryRepositoryImpl(
    private val inventoryJpaRepository: InventoryJpaRepository,
) : InventoryRepository {
    override fun save(inventory: Inventory): Inventory {
        return inventoryJpaRepository.save(inventory)
    }

    override fun findByProductId(productId: Long): Optional<Inventory> {
        return inventoryJpaRepository.findById(productId)
    }

    override fun findByProductIdForUpdate(productId: Long): Optional<Inventory> {
        return inventoryJpaRepository.findByProductIdForUpdate(productId)
    }

    override fun delete(inventory: Inventory) {
        inventoryJpaRepository.delete(inventory)
    }
}
