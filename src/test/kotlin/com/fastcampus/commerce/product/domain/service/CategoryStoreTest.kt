package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.ProductCategory
import com.fastcampus.commerce.product.domain.error.ProductErrorCode
import com.fastcampus.commerce.product.domain.repository.ProductCategoryRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify

class CategoryStoreTest : FunSpec(
    {
        val categoryReader = mockk<CategoryReader>()
        val productCategoryRepository = mockk<ProductCategoryRepository>()
        val categoryStore = CategoryStore(categoryReader, productCategoryRepository)

        beforeTest {
            clearMocks(categoryReader, productCategoryRepository)
        }

        context("mappingProductCategories") {

            val productId = 100L
            val categoryIds = listOf(1L, 2L, 3L)

            test("모든 카테고리가 존재하면 ProductCategory들을 저장하고 반환한다.") {
                every { categoryReader.countCategoriesByIds(categoryIds) } returns categoryIds.size.toLong()
                val expectedProductCategories = categoryIds.map { ProductCategory(productId = productId, categoryId = it) }
                every {
                    productCategoryRepository.saveAll(
                        match { actual ->
                            actual.size == expectedProductCategories.size &&
                                actual.zip(expectedProductCategories).all { (a, b) ->
                                    a.productId == b.productId && a.categoryId == b.categoryId
                                }
                        },
                    )
                } returns expectedProductCategories

                val result = categoryStore.mappingProductCategories(productId, categoryIds)

                result shouldHaveSize expectedProductCategories.size
                result.map { it.categoryId } shouldContainExactly categoryIds

                verify(exactly = 1) { categoryReader.countCategoriesByIds(categoryIds) }
                verify(exactly = 1) {
                    productCategoryRepository.saveAll(
                        match { actual ->
                            actual.size == expectedProductCategories.size &&
                                actual.zip(expectedProductCategories).all { (a, b) ->
                                    a.productId == b.productId && a.categoryId == b.categoryId
                                }
                        },
                    )
                }
            }

            test("일부 카테고리가 존재하지 않으면 INVALID_CATEGORY 예외를 던진다.") {
                every { categoryReader.countCategoriesByIds(categoryIds) } returns categoryIds.size - 1L

                shouldThrow<CoreException> {
                    categoryStore.mappingProductCategories(productId, categoryIds)
                }.errorCode shouldBe ProductErrorCode.INVALID_CATEGORY

                verify(exactly = 1) { categoryReader.countCategoriesByIds(categoryIds) }
                verify(exactly = 0) { productCategoryRepository.saveAll(any()) }
            }

            test("조회된 카테고리 개수가 요청보다 많은 경우도 INVALID_CATEGORY 예외를 던진다.") {
                every { categoryReader.countCategoriesByIds(categoryIds) } returns categoryIds.size + 1L

                shouldThrow<CoreException> {
                    categoryStore.mappingProductCategories(productId, categoryIds)
                }.errorCode shouldBe ProductErrorCode.INVALID_CATEGORY

                verify(exactly = 1) { categoryReader.countCategoriesByIds(categoryIds) }
                verify(exactly = 0) { productCategoryRepository.saveAll(any()) }
            }
        }

        context("removeProductCategories") {
            test("상품 ID로 연관된 카테고리들을 조회하고 모두 삭제할 수 있다.") {
                val productId = 1L
                val productCategories = listOf(
                    ProductCategory(productId = productId, categoryId = 10L),
                    ProductCategory(productId = productId, categoryId = 20L),
                )
                every { categoryReader.getAllProductCategoriesByProductId(productId) } returns productCategories
                every { productCategoryRepository.deleteAll(productCategories) } just Runs

                categoryStore.removeProductCategories(productId)

                verify(exactly = 1) { categoryReader.getAllProductCategoriesByProductId(productId) }
                verify(exactly = 1) { productCategoryRepository.deleteAll(productCategories) }
            }
        }
    },
)
