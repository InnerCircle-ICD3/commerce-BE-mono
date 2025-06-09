package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.QCategory.category
import com.fastcampus.commerce.product.domain.entity.QCategoryGroup.categoryGroup
import com.fastcampus.commerce.product.domain.model.CategoryDetail
import com.fastcampus.commerce.product.domain.model.QCategoryDetail
import com.fastcampus.commerce.product.domain.repository.CategoryRepository
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryImpl(
    private val categoryJpaRepository: CategoryJpaRepository,
    private val queryFactory: JPAQueryFactory,
) : CategoryRepository {
    override fun countCategoriesByIds(categoryIds: List<Long>): Long {
        return categoryJpaRepository.countCategoriesByIdIn(categoryIds)
    }

    override fun getCategoryDetail(): List<CategoryDetail> {
        return queryFactory
            .select(
                QCategoryDetail(
                    categoryGroup.id,
                    categoryGroup.title,
                    category.id,
                    category.name,
                    category.sortOrder,
                ),
            )
            .from(category)
            .join(categoryGroup).on(category.groupId.eq(categoryGroup.id))
            .orderBy(categoryGroup.id.asc(), category.sortOrder.asc())
            .fetch()
    }
}
