package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryJpaRepository : JpaRepository<Category, Long> {
    fun countCategoriesByIdIn(categoryIds: List<Long>): Long
}
