package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface InventoryJpaRepository : JpaRepository<Inventory, Long>{
    fun findByProductId(productId: Long): Optional<Inventory>
}
