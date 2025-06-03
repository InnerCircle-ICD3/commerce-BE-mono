package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.product.domain.error.ProductErrorCode
import com.fastcampus.commerce.product.domain.model.ProductRegister
import com.fastcampus.commerce.product.domain.model.ProductUpdater
import com.fastcampus.commerce.product.domain.repository.InventoryRepository
import com.fastcampus.commerce.product.domain.repository.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder

class ProductStoreTest : FunSpec(
    {
        val productRepository = mockk<ProductRepository>()
        val inventoryRepository = mockk<InventoryRepository>()
        val productReader = mockk<ProductReader>()
        val productStore = ProductStore(
            productRepository = productRepository,
            inventoryRepository = inventoryRepository,
            productReader = productReader,
        )

        beforeTest {
            clearMocks(productRepository, inventoryRepository, productReader)
        }

        test("상품과 재고를 함께 저장한다.") {
            val expectedProductId = 1L
            val expectedName = "콜드브루"
            val expectedPrice = 3500
            val expectedQuantity = 100
            val expectedThumbnail = "https://test.com/thumbnail.png"
            val expectedDetailImage = "https://test.com/detailImage.png"

            val register = ProductRegister(
                name = expectedName,
                price = expectedPrice,
                quantity = expectedQuantity,
                thumbnail = expectedThumbnail,
                detailImage = expectedDetailImage,
                categoryIds = listOf(1L, 2L, 3L),
                registerId = 1L,
            )

            val product = register.toProduct().apply { id = expectedProductId }
            val inventory = Inventory(productId = expectedProductId, quantity = expectedQuantity)

            every { productRepository.save(any<Product>()) } returns product
            every { inventoryRepository.save(any<Inventory>()) } returns inventory

            val result = productStore.saveProductWithInventory(register)

            result shouldBe product

            verify(exactly = 1) { productRepository.save(any<Product>()) }
            verify(exactly = 1) { inventoryRepository.save(any<Inventory>()) }
        }

        test("상품 업데이트 시 상품을 조회하고, 상태를 업데이트한다") {
            val productId = 1L
            val updater = ProductUpdater(
                id = productId,
                name = "아메리카노",
                price = 3000,
                quantity = 100,
                detailImage = "https://test.com/detail.png",
                thumbnail = "https://test.com/thumb.png",
                categoryIds = listOf(1L, 2L),
                status = SellingStatus.UNAVAILABLE,
                updaterId = 10L,
            )

            val product = spyk(
                Product(
                    name = "old name",
                    price = 1000,
                    thumbnail = "old-thumb",
                    detailImage = "old-detail",
                    status = SellingStatus.ON_SALE,
                ),
            ).apply { id = productId }
            every { productReader.getProductById(updater.id) } returns product

            productStore.updateProduct(updater)

            product.name shouldBe updater.name
            product.price shouldBe updater.price
            product.thumbnail shouldBe updater.thumbnail
            product.detailImage shouldBe updater.detailImage
            product.status shouldBe updater.status

            verify(exactly = 1) { productReader.getProductById(updater.id) }
            verify(exactly = 1) { product.update(updater) }
        }

        test("상품이 존재하지 않으면 PRODUCT_NOT_FOUND 예외가 발생한다.") {
            val productId = 1L
            val updater = ProductUpdater(
                id = productId,
                name = "아메리카노",
                price = 3000,
                quantity = 100,
                detailImage = "https://test.com/detail.png",
                thumbnail = "https://test.com/thumb.png",
                categoryIds = listOf(1L, 2L),
                status = SellingStatus.UNAVAILABLE,
                updaterId = 10L,
            )
            every { productReader.getProductById(productId) } throws CoreException(ProductErrorCode.PRODUCT_NOT_FOUND)

            shouldThrow<CoreException> {
                productStore.updateProduct(updater)
            }.errorCode shouldBe ProductErrorCode.PRODUCT_NOT_FOUND

            verify(exactly = 1) { productReader.getProductById(productId) }
        }

        test("재고 수량 업데이트 시 해당 상품의 인벤토리를 조회하고 수량을 변경한다.") {
            val productId = 1L
            val originalQuantity = 100
            val inventory = spyk(Inventory(productId = productId, quantity = originalQuantity))
            every { productReader.getInventoryByProductIdForUpdate(productId) } returns inventory
            val quantity = 50

            productStore.updateQuantityByProductId(productId, quantity)

            inventory.quantity shouldBe quantity

            verify(exactly = 1) { productReader.getInventoryByProductIdForUpdate(productId) }
            verify(exactly = 1) { inventory.updateQuantity(quantity) }
        }

        test("재고가 존재하지 않으면 INVENTORY_NOT_FOUND 예외가 발생한다.") {
            val productId = 1L
            val quantity = 10
            every { productReader.getInventoryByProductIdForUpdate(productId) } throws CoreException(ProductErrorCode.INVENTORY_NOT_FOUND)

            shouldThrow<CoreException> {
                productStore.updateQuantityByProductId(productId, quantity)
            }.errorCode shouldBe ProductErrorCode.INVENTORY_NOT_FOUND

            verify(exactly = 1) { productReader.getInventoryByProductIdForUpdate(productId) }
        }

        test("상품과 재고를 삭제할 수 있다.") {
            val productId = 1L
            val product = mockk<Product>()
            val inventory = mockk<Inventory>()
            every { productReader.getProductById(productId) } returns product
            every { productRepository.delete(product) } just Runs
            every { productReader.getInventoryByProductId(productId) } returns inventory
            every { inventoryRepository.delete(inventory) } just Runs

            productStore.deleteProductWithInventory(productId)

            verifyOrder {
                productReader.getProductById(productId)
                productRepository.delete(product)
                productReader.getInventoryByProductId(productId)
                inventoryRepository.delete(inventory)
            }
        }
    },
)
