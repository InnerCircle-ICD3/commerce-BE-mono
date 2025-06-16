package com.fastcampus.commerce.product.application

import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.product.domain.model.ProductCategoryInfo
import com.fastcampus.commerce.product.domain.model.ProductInfo
import com.fastcampus.commerce.product.domain.service.CategoryReader
import com.fastcampus.commerce.product.domain.service.ProductReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class MainProductServiceTest : FunSpec(
    {
        val timeProvider = mockk<TimeProvider>()
        val productReader = mockk<ProductReader>()
        val categoryReader = mockk<CategoryReader>()
        val mainProductService = MainProductService(timeProvider, productReader, categoryReader)

        beforeTest {
            clearMocks(timeProvider, productReader, categoryReader)
        }

        context("신상품 조회") {
            test("최신 상품 5개를 조회하고 카테고리 정보와 함께 반환한다") {
                val limit = 5
                val product1 = ProductInfo(
                    id = 1L,
                    name = "신상품1",
                    price = 10000,
                    quantity = 100,
                    thumbnail = "thumb1.jpg",
                    detailImage = "detail1.jpg",
                    status = SellingStatus.ON_SALE,
                )
                val product2 = ProductInfo(
                    id = 2L,
                    name = "신상품2",
                    price = 10000,
                    quantity = 100,
                    thumbnail = "thumb2.jpg",
                    detailImage = "detail2.jpg",
                    status = SellingStatus.ON_SALE,
                    )
                val productInfos = listOf(product1, product2)

                val categoryMap = mapOf(
                    1L to ProductCategoryInfo(
                        intensity = "Strong",
                        cupSize = "Large",
                    ),
                    2L to ProductCategoryInfo(
                        intensity = "Mild",
                        cupSize = "Small",
                    )
                )

                every { productReader.findLatestProducts(limit) } returns productInfos
                every { categoryReader.getProductCategoryMap(listOf(1L, 2L)) } returns categoryMap

                val result = mainProductService.getNewProducts()

                result.size shouldBe 2
                result[0].id shouldBe 1L
                result[0].name shouldBe "신상품1"
                result[0].price shouldBe 10000
                result[0].quantity shouldBe 100
                result[0].thumbnail shouldBe  "thumb1.jpg"
                result[0].detailImage shouldBe  "detail1.jpg"
                result[0].intensity shouldBe "Strong"
                result[0].cupSize shouldBe "Large"

                result[1].id shouldBe 2L
                result[1].name shouldBe "신상품2"
                result[1].price shouldBe 10000
                result[1].quantity shouldBe 100
                result[1].thumbnail shouldBe  "thumb2.jpg"
                result[1].detailImage shouldBe  "detail2.jpg"
                result[1].intensity shouldBe "Mild"
                result[1].cupSize shouldBe "Small"

                verify(exactly = 1) { productReader.findLatestProducts(limit) }
                verify(exactly = 1) { categoryReader.getProductCategoryMap(listOf(1L, 2L)) }
            }

            test("신상품이 없는 경우 빈 리스트를 반환한다") {
                val limit = 5
                every { productReader.findLatestProducts(limit) } returns emptyList()
                every { categoryReader.getProductCategoryMap(emptyList()) } returns emptyMap()

                val result = mainProductService.getNewProducts()

                result shouldBe emptyList()

                verify(exactly = 1) { productReader.findLatestProducts(limit) }
                verify(exactly = 1) { categoryReader.getProductCategoryMap(emptyList()) }
            }
        }

        context("베스트 상품 조회") {
            test("최근 일주일간 판매량이 높은 상품 5개를 조회하고 카테고리 정보와 함께 반환한다") {
                val limit = 5
                val now = LocalDateTime.of(2024, 1, 15, 12, 0, 0)
                val oneWeekAgo = now.minusDays(7)

                val product1 = ProductInfo(
                    id = 1L,
                    name = "신상품1",
                    price = 10000,
                    quantity = 100,
                    thumbnail = "thumb1.jpg",
                    detailImage = "detail1.jpg",
                    status = SellingStatus.ON_SALE,
                )
                val product2 = ProductInfo(
                    id = 2L,
                    name = "신상품2",
                    price = 10000,
                    quantity = 100,
                    thumbnail = "thumb2.jpg",
                    detailImage = "detail2.jpg",
                    status = SellingStatus.ON_SALE,
                )
                val productInfos = listOf(product1, product2)

                val categoryMap = mapOf(
                    1L to ProductCategoryInfo(
                        intensity = "Strong",
                        cupSize = "Large",
                    ),
                    2L to ProductCategoryInfo(
                        intensity = "Mild",
                        cupSize = "Small",
                    )
                )

                every { timeProvider.now() } returns now
                every { productReader.findBestProducts(oneWeekAgo, limit) } returns productInfos
                every { categoryReader.getProductCategoryMap(listOf(1L, 2L)) } returns categoryMap

                val result = mainProductService.getBestProducts()

                result.size shouldBe 2
                result[0].id shouldBe 1L
                result[0].name shouldBe "신상품1"
                result[0].price shouldBe 10000
                result[0].quantity shouldBe 100
                result[0].thumbnail shouldBe  "thumb1.jpg"
                result[0].detailImage shouldBe  "detail1.jpg"
                result[0].intensity shouldBe "Strong"
                result[0].cupSize shouldBe "Large"

                result[1].id shouldBe 2L
                result[1].name shouldBe "신상품2"
                result[1].price shouldBe 10000
                result[1].quantity shouldBe 100
                result[1].thumbnail shouldBe  "thumb2.jpg"
                result[1].detailImage shouldBe  "detail2.jpg"
                result[1].intensity shouldBe "Mild"
                result[1].cupSize shouldBe "Small"

                verify(exactly = 1) { timeProvider.now() }
                verify(exactly = 1) { productReader.findBestProducts(oneWeekAgo, limit) }
                verify(exactly = 1) { categoryReader.getProductCategoryMap(listOf(1L, 2L)) }
            }

            test("베스트 상품이 없는 경우 빈 리스트를 반환한다") {
                val limit = 5
                val now = LocalDateTime.of(2024, 1, 15, 12, 0, 0)
                val oneWeekAgo = now.minusDays(7)

                every { timeProvider.now() } returns now
                every { productReader.findBestProducts(oneWeekAgo, limit) } returns emptyList()
                every { categoryReader.getProductCategoryMap(emptyList()) } returns emptyMap()

                val result = mainProductService.getBestProducts()

                result shouldBe emptyList()

                verify(exactly = 1) { timeProvider.now() }
                verify(exactly = 1) { productReader.findBestProducts(oneWeekAgo, limit) }
                verify(exactly = 1) { categoryReader.getProductCategoryMap(emptyList()) }
            }
        }
    }
)
