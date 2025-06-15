package com.fastcampus.commerce.order.infrastructure.repository

import com.fastcampus.commerce.order.domain.entity.ProductSnapshot
import org.springframework.data.jpa.repository.JpaRepository

interface ProductSnapshotRepository : JpaRepository<ProductSnapshot, Long>
