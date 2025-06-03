package com.fastcampus.commerce.cart.infrastructure.repository

import com.fastcampus.commerce.product.domain.entity.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InventoryRepository : JpaRepository<Inventory, Long> {
    fun findByProductId(productId: Long): Inventory?
}
