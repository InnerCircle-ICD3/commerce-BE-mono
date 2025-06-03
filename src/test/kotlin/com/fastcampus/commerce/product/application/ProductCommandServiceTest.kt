package com.fastcampus.commerce.product.application

import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.product.domain.model.ProductRegister
import com.fastcampus.commerce.product.domain.model.ProductUpdater
import com.fastcampus.commerce.product.domain.service.CategoryStore
import com.fastcampus.commerce.product.domain.service.ProductStore
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify

class ProductCommandServiceTest : FunSpec(
    {
        val productStore = mockk<ProductStore>()
        val categoryStore = mockk<CategoryStore>()
        val productCommandService = ProductCommandService(productStore, categoryStore)

        test("상품 등록 시 상품 저장과 카테고리 매핑을 수행하고 상품 ID를 반환한다.") {
            val expectedProductId = 1L
            val expectedName = "콜드브루"
            val expectedPrice = 3500
            val expectedQuantity = 100
            val expectedThumbnail = "https://test.com/thumb.png"
            val expectedDetailImage = "https://test.com/detail.png"
            val expectedCategoryIds = listOf(1L, 2L)

            val register = ProductRegister(
                name = expectedName,
                price = expectedPrice,
                quantity = expectedQuantity,
                thumbnail = expectedThumbnail,
                detailImage = expectedDetailImage,
                categoryIds = expectedCategoryIds,
                registerId = 999L,
            )

            val product = Product(
                name = expectedName,
                price = expectedPrice,
                thumbnail = expectedThumbnail,
                detailImage = expectedDetailImage,
            ).apply {
                id = expectedProductId
            }
            every { productStore.saveProductWithInventory(register) } returns product
            every { categoryStore.mappingProductCategories(expectedProductId, expectedCategoryIds) } returns emptyList()

            val result = productCommandService.register(register)

            result shouldBe expectedProductId

            verify(exactly = 1) { productStore.saveProductWithInventory(register) }
            verify(exactly = 1) { categoryStore.mappingProductCategories(expectedProductId, expectedCategoryIds) }
        }

        test("상품 ID를 상품 정보를 수정할 수 있다.") {
            val updater = ProductUpdater(
                id = 1L,
                name = "아메리카노",
                price = 3000,
                quantity = 100,
                detailImage = "https://test.com/detail.png",
                thumbnail = "https://test.com/thumb.png",
                categoryIds = listOf(1L, 2L),
                status = SellingStatus.ON_SALE,
                updaterId = 10L,
            )
            every { productStore.updateProduct(updater) } just Runs

            productCommandService.updateProduct(updater)

            verify(exactly = 1) { productStore.updateProduct(updater) }
        }

        test("상품 ID를 기반으로 재고를 수정할 수 있다.") {
            val updater = ProductUpdater(
                id = 1L,
                name = "아메리카노",
                price = 3000,
                quantity = 150,
                detailImage = "https://test.com/detail.png",
                thumbnail = "https://test.com/thumb.png",
                categoryIds = listOf(1L, 2L),
                status = SellingStatus.ON_SALE,
                updaterId = 10L,
            )
            every { productStore.updateQuantityByProductId(updater.id, updater.quantity) } just Runs

            productCommandService.updateInventory(updater)

            verify(exactly = 1) { productStore.updateQuantityByProductId(updater.id, updater.quantity) }
        }
    },
)
