package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository

interface ProductCategoryJpaRepository : JpaRepository<ProductCategory, Long>
