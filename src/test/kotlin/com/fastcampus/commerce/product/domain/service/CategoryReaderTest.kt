package com.fastcampus.commerce.product.domain.service
import com.fastcampus.commerce.product.domain.repository.CategoryRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CategoryReaderTest : FunSpec({

    val categoryRepository = mockk<CategoryRepository>()
    val categoryReader = CategoryReader(categoryRepository)

    beforeTest {
        clearMocks(categoryRepository)
    }

    test("카테고리 ID 목록으로 카운트를 조회할 수 있다") {
        val categoryIds = listOf(1L, 2L, 3L)
        val expectedCount = 3L

        every { categoryRepository.countCategoriesByIds(categoryIds) } returns expectedCount

        val result = categoryReader.countCategoriesByIds(categoryIds)

        result shouldBe expectedCount

        verify(exactly = 1) { categoryRepository.countCategoriesByIds(categoryIds) }
    }
})
