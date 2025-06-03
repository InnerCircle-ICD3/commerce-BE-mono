package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.Inventory
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryJpaRepository : JpaRepository<Inventory, Long>
