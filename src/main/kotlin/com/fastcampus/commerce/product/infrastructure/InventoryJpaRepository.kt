package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.Optional
import jakarta.persistence.LockModeType

interface InventoryJpaRepository : JpaRepository<Inventory, Long> {
    fun findByProductId(productId: Long): Optional<Inventory>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByProductIdForUpdate(productId: Long): Optional<Inventory>
}
