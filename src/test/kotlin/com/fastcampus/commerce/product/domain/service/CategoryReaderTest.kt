package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.product.domain.entity.ProductCategory
import com.fastcampus.commerce.product.domain.model.CategoryInfo
import com.fastcampus.commerce.product.domain.model.ProductCategoryInfo
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

        context("countCategoriesByIds") {
            test("카테고리 ID 목록으로 카운트를 조회할 수 있다") {
                val categoryIds = listOf(1L, 2L, 3L)
                val expectedCount = 3L

                every { categoryRepository.countCategoriesByIds(categoryIds) } returns expectedCount

                val result = categoryReader.countCategoriesByIds(categoryIds)

                result shouldBe expectedCount

                verify(exactly = 1) { categoryRepository.countCategoriesByIds(categoryIds) }
            }
        }
        context("getAllProductCategoriesByProductId") {
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
        }

        context("getProductCategoryMap") {
            test("상품 ID 목록으로 상품 카테고리 맵을 조회할 수 있다") {
                val productIds = listOf(1L, 2L)
                val categoryInfos = listOf(
                    CategoryInfo(
                        productId = 1L,
                        groupId = 1L,
                        groupTitle = "intensity",
                        categoryId = 10L,
                        categoryName = "Strong",
                        sortOrder = 1,
                    ),
                    CategoryInfo(
                        productId = 1L,
                        groupId = 2L,
                        groupTitle = "cup_size",
                        categoryId = 20L,
                        categoryName = "Large",
                        sortOrder = 1,
                    ),
                    CategoryInfo(
                        productId = 2L,
                        groupId = 1L,
                        groupTitle = "intensity",
                        categoryId = 11L,
                        categoryName = "Mild",
                        sortOrder = 2,
                    ),
                )
                val expectedMap = mapOf(
                    1L to ProductCategoryInfo(intensity = "Strong", cupSize = "Large"),
                    2L to ProductCategoryInfo(intensity = "Mild", cupSize = ""),
                )
                every { productCategoryRepository.getCategoryInfosIn(productIds) } returns categoryInfos

                val result = categoryReader.getProductCategoryMap(productIds)

                result shouldBe expectedMap

                verify(exactly = 1) { productCategoryRepository.getCategoryInfosIn(productIds) }
            }

            test("상품 ID 목록이 빈 경우 빈 맵을 반환한다") {
                val productIds = emptyList<Long>()
                val expectedMap = emptyMap<Long, ProductCategoryInfo>()
                every { productCategoryRepository.getCategoryInfosIn(productIds) } returns emptyList()

                val result = categoryReader.getProductCategoryMap(productIds)

                result shouldBe expectedMap

                verify(exactly = 1) { productCategoryRepository.getCategoryInfosIn(productIds) }
            }
        }
    },
)
