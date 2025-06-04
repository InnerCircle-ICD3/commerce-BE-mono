package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import jakarta.persistence.LockModeType

interface InventoryJpaRepository : JpaRepository<Inventory, Long> {
    fun findByProductId(productId: Long): Optional<Inventory>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Inventory c where c.productId = :productId")
    fun findByProductIdForUpdate(productId: Long): Optional<Inventory>
}
