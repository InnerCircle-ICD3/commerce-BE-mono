package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.Optional
import jakarta.persistence.LockModeType

interface InventoryJpaRepository : JpaRepository<Inventory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByProductidForUpdate(productId: Long): Optional<Inventory>
}
