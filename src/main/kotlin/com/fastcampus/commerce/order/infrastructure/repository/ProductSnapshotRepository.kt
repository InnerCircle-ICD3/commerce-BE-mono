package com.fastcampus.commerce.order.infrastructure.repository

import com.fastcampus.commerce.order.domain.entity.ProductSnapshot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface ProductSnapshotRepository : JpaRepository<ProductSnapshot, Long> {
    @Query("SELECT ps FROM ProductSnapshot ps WHERE ps.productId = :productId ORDER BY ps.createdAt DESC limit 1")
    fun findByProductId(productId: Long): Optional<ProductSnapshot>
}
