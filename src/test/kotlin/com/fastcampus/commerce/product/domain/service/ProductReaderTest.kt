package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.entity.Product
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
    },
)
