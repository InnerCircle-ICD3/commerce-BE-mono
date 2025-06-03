package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.repository.InventoryRepository
import com.fastcampus.commerce.product.domain.repository.ProductRepository
import com.fastcampus.commerce.product.domain.model.ProductRegister
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ProductStoreTest : FunSpec(
    {
        val productRepository = mockk<ProductRepository>()
        val inventoryRepository = mockk<InventoryRepository>()
        val productStore = ProductStore(productRepository, inventoryRepository)

        beforeTest {
            clearMocks(productRepository, inventoryRepository)
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
    },
)
