package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.product.domain.error.ProductErrorCode
import com.fastcampus.commerce.product.domain.model.ProductInfo
import com.fastcampus.commerce.product.domain.model.SearchProductCondition
import com.fastcampus.commerce.product.domain.repository.InventoryRepository
import com.fastcampus.commerce.product.domain.repository.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.Optional

class ProductReaderTest : FunSpec(
    {
        val productRepository = mockk<ProductRepository>()
        val inventoryRepository = mockk<InventoryRepository>()
        val productReader = ProductReader(
            productRepository = productRepository,
            inventoryRepository = inventoryRepository,
        )

        beforeTest {
            clearMocks(productRepository, inventoryRepository)
        }

        context("getInventoryByProductIdForUpdate") {
            test("상품 ID로 재고를 조회할 수 있다") {
                val productId = 1L
                val expectedInventory = Inventory(productId = productId, quantity = 100)

                every { inventoryRepository.findByProductId(productId) } returns Optional.of(expectedInventory)

                val result = productReader.getInventoryByProductId(productId)

                result shouldBe expectedInventory

                verify(exactly = 1) { inventoryRepository.findByProductId(productId) }
            }

            test("상품 ID로 재고를 조회할 때 재고가 없으면 INVENTORY_NOT_FOUND 예외가 발생한다") {
                val productId = 1L

                every { inventoryRepository.findByProductId(productId) } returns Optional.empty()

                shouldThrow<CoreException> {
                    productReader.getInventoryByProductId(productId)
                }.errorCode shouldBe ProductErrorCode.INVENTORY_NOT_FOUND

                verify(exactly = 1) { inventoryRepository.findByProductId(productId) }
            }
        }

        context("getProductById") {

            test("상품 ID로 상품을 조회할 수 있다") {
                val productId = 1L
                val expectedProduct = Product(
                    name = "콜드브루",
                    price = 3500,
                    thumbnail = "https://test.com/thumbnail.png",
                    detailImage = "https://test.com/detailImage.png",
                ).apply { id = productId }

                every { productRepository.findById(productId) } returns Optional.of(expectedProduct)

                val result = productReader.getProductById(productId)

                result shouldBe expectedProduct

                verify(exactly = 1) { productRepository.findById(productId) }
            }

            test("상품 ID로 상품을 조회할 때 상품이 없으면 PRODUCT_NOT_FOUND 예외가 발생한다") {
                val productId = 1L

                every { productRepository.findById(productId) } returns Optional.empty()

                shouldThrow<CoreException> {
                    productReader.getProductById(productId)
                }.errorCode shouldBe ProductErrorCode.PRODUCT_NOT_FOUND

                verify(exactly = 1) { productRepository.findById(productId) }
            }
        }

        context("searchProducts") {

            test("검색 조건으로 상품 목록을 조회할 수 있다") {
                val condition = SearchProductCondition(
                    name = "콜드브루",
                    categories = listOf(1L, 2L),
                )
                val pageable = PageRequest.of(0, 10)
                val expectedProductInfos = listOf(
                    ProductInfo(
                        id = 1L,
                        name = "콜드브루",
                        price = 3500,
                        quantity = 100,
                        thumbnail = "https://test.com/thumbnail.png",
                        detailImage = "https://test.com/detailImage.png",
                        status = SellingStatus.ON_SALE,
                    ),
                )
                val expectedPage = PageImpl(expectedProductInfos, pageable, 1L)
                every { productRepository.searchProducts(condition, pageable) } returns expectedPage

                val result = productReader.searchProducts(condition, pageable)

                result shouldBe expectedPage

                verify(exactly = 1) { productRepository.searchProducts(condition, pageable) }
            }

            test("검색 조건에 해당하는 상품이 없으면 빈 결과를 반환한다") {
                val condition = SearchProductCondition(
                    name = "존재하지않는상품",
                )
                val pageable = PageRequest.of(0, 20)
                val expectedPage = PageImpl<ProductInfo>(emptyList(), pageable, 0L)

                every { productRepository.searchProducts(condition, pageable) } returns expectedPage

                val result = productReader.searchProducts(condition, pageable)

                result.content shouldBe emptyList()
                result.totalElements shouldBe 0L

                verify(exactly = 1) { productRepository.searchProducts(condition, pageable) }
            }
        }

        context("getProductInfo") {
            test("상품 ID로 상품 정보를 조회할 수 있다") {
                val productId = 1L
                val product = Product(
                    name = "콜드브루",
                    price = 3500,
                    thumbnail = "https://test.com/thumbnail.png",
                    detailImage = "https://test.com/detailImage.png",
                ).apply { id = productId }
                val inventory = Inventory(productId = productId, quantity = 100)
                val expectedProductInfo = ProductInfo(
                    id = productId,
                    name = "콜드브루",
                    price = 3500,
                    quantity = 100,
                    thumbnail = "https://test.com/thumbnail.png",
                    detailImage = "https://test.com/detailImage.png",
                    status = SellingStatus.ON_SALE,
                )

                every { productRepository.findById(productId) } returns Optional.of(product)
                every { inventoryRepository.findByProductId(productId) } returns Optional.of(inventory)

                val result = productReader.getProductInfo(productId)

                result shouldBe expectedProductInfo

                verify(exactly = 1) { productRepository.findById(productId) }
                verify(exactly = 1) { inventoryRepository.findByProductId(productId) }
            }

            test("상품이 존재하지 않으면 PRODUCT_NOT_FOUND 예외가 발생한다") {
                val productId = 1L

                every { productRepository.findById(productId) } returns Optional.empty()

                shouldThrow<CoreException> {
                    productReader.getProductInfo(productId)
                }.errorCode shouldBe ProductErrorCode.PRODUCT_NOT_FOUND

                verify(exactly = 1) { productRepository.findById(productId) }
                verify(exactly = 0) { inventoryRepository.findByProductId(any()) }
            }

            test("재고가 존재하지 않으면 INVENTORY_NOT_FOUND 예외가 발생한다") {
                val productId = 1L
                val product = Product(
                    name = "콜드브루",
                    price = 3500,
                    thumbnail = "https://test.com/thumbnail.png",
                    detailImage = "https://test.com/detailImage.png",
                ).apply { id = productId }

                every { productRepository.findById(productId) } returns Optional.of(product)
                every { inventoryRepository.findByProductId(productId) } returns Optional.empty()

                shouldThrow<CoreException> {
                    productReader.getProductInfo(productId)
                }.errorCode shouldBe ProductErrorCode.INVENTORY_NOT_FOUND

                verify(exactly = 1) { productRepository.findById(productId) }
                verify(exactly = 1) { inventoryRepository.findByProductId(productId) }
            }
        }

        context("findLatestProducts") {
            test("최신 상품 목록을 조회할 수 있다") {
                val limit = 10
                val expectedProductInfos = listOf(
                    ProductInfo(
                        id = 1L,
                        name = "아메리카노",
                        price = 3000,
                        quantity = 50,
                        thumbnail = "https://test.com/americano.png",
                        detailImage = "https://test.com/americano-detail.png",
                        status = SellingStatus.ON_SALE,
                    ),
                    ProductInfo(
                        id = 2L,
                        name = "카페라떼",
                        price = 4000,
                        quantity = 30,
                        thumbnail = "https://test.com/latte.png",
                        detailImage = "https://test.com/latte-detail.png",
                        status = SellingStatus.ON_SALE,
                    ),
                )

                every { productRepository.findLatestProducts(limit) } returns expectedProductInfos

                val result = productReader.findLatestProducts(limit)

                result shouldBe expectedProductInfos

                verify(exactly = 1) { productRepository.findLatestProducts(limit) }
            }

            test("limit이 0인 경우 빈 리스트를 반환한다") {
                val limit = 0

                every { productRepository.findLatestProducts(limit) } returns emptyList()

                val result = productReader.findLatestProducts(limit)

                result shouldBe emptyList()

                verify(exactly = 1) { productRepository.findLatestProducts(limit) }
            }
        }

        context("findBestProducts") {
            test("베스트 상품 목록을 조회할 수 있다") {
                val baseDate = LocalDateTime.of(2024, 1, 1, 0, 0)
                val limit = 5
                val expectedProductInfos = listOf(
                    ProductInfo(
                        id = 3L,
                        name = "아이스 아메리카노",
                        price = 3500,
                        quantity = 100,
                        thumbnail = "https://test.com/ice-americano.png",
                        detailImage = "https://test.com/ice-americano-detail.png",
                        status = SellingStatus.ON_SALE,
                    ),
                    ProductInfo(
                        id = 4L,
                        name = "카푸치노",
                        price = 4500,
                        quantity = 20,
                        thumbnail = "https://test.com/cappuccino.png",
                        detailImage = "https://test.com/cappuccino-detail.png",
                        status = SellingStatus.ON_SALE,
                    ),
                )

                every { productRepository.findBestProducts(baseDate, limit) } returns expectedProductInfos

                val result = productReader.findBestProducts(baseDate, limit)

                result shouldBe expectedProductInfos

                verify(exactly = 1) { productRepository.findBestProducts(baseDate, limit) }
            }

            test("베스트 상품이 없으면 빈 리스트를 반환한다") {
                val baseDate = LocalDateTime.of(2024, 1, 1, 0, 0)
                val limit = 10

                every { productRepository.findBestProducts(baseDate, limit) } returns emptyList()

                val result = productReader.findBestProducts(baseDate, limit)

                result shouldBe emptyList()

                verify(exactly = 1) { productRepository.findBestProducts(baseDate, limit) }
            }
        }
    },
)
