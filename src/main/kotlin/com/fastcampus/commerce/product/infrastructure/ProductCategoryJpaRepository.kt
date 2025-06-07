package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.ProductCategory
import com.fastcampus.commerce.product.domain.model.CategoryInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProductCategoryJpaRepository : JpaRepository<ProductCategory, Long> {
    fun findAllByProductId(productId: Long): List<ProductCategory>

    @Query(
        """
    select new com.fastcampus.commerce.product.domain.model.CategoryInfo(
        p.id,
        g.id,
        g.title,
        c.id,
        c.name,
        c.sortOrder
    )
    from ProductCategory pc
        join Product p on pc.productId = p.id
        join Category c on pc.categoryId = c.id
        join CategoryGroup g on c.groupId = g.id
    where pc.isDeleted = false
    and p.id in :productIds
    """,
    )
    fun findCategoriesByProductIds(productIds: List<Long>): List<CategoryInfo>
}
