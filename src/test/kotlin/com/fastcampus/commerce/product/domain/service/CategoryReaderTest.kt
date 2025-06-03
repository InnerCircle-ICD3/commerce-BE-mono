package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.product.domain.entity.ProductCategory
import com.fastcampus.commerce.product.domain.repository.CategoryRepository
import com.fastcampus.commerce.product.domain.repository.ProductCategoryRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CategoryReaderTest : FunSpec(
    {

        val categoryRepository = mockk<CategoryRepository>()
        val productCategoryRepository = mockk<ProductCategoryRepository>()
        val categoryReader = CategoryReader(
            categoryRepository = categoryRepository,
            productCategoryRepository = productCategoryRepository,
        )

        beforeTest {
            clearMocks(categoryRepository, productCategoryRepository)
        }

        test("카테고리 ID 목록으로 카운트를 조회할 수 있다") {
            val categoryIds = listOf(1L, 2L, 3L)
            val expectedCount = 3L

            every { categoryRepository.countCategoriesByIds(categoryIds) } returns expectedCount

            val result = categoryReader.countCategoriesByIds(categoryIds)

            result shouldBe expectedCount

            verify(exactly = 1) { categoryRepository.countCategoriesByIds(categoryIds) }
        }

        test("상품 ID로 연관된 카테고리들을 조회할 수 있다.") {
            val productId = 1L
            val productCategories = listOf(
                ProductCategory(productId = productId, categoryId = 10L),
                ProductCategory(productId = productId, categoryId = 20L),
            )
            every { categoryReader.getAllProductCategoriesByProductId(productId) } returns productCategories

            categoryReader.getAllProductCategoriesByProductId(productId)

            verify(exactly = 1) { categoryReader.getAllProductCategoriesByProductId(productId) }
        }
    },
)
