package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<Product, Long>
