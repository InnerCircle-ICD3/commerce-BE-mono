package com.fastcampus.commerce.order.infrastructure.repository

import com.fastcampus.commerce.order.domain.entity.ProductSnapshot
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ProductSnapshotRepository : JpaRepository<ProductSnapshot, Long> {
    fun findByProductId(productId: Long): Optional<ProductSnapshot>
}
