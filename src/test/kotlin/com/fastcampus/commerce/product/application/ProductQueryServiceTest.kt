package com.fastcampus.commerce.product.application

import com.fastcampus.commerce.product.application.request.SearchProductRequest
import com.fastcampus.commerce.product.application.response.CategoryResponse
import com.fastcampus.commerce.product.application.response.SearchProductResponse
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.product.domain.model.CategoryDetail
import com.fastcampus.commerce.product.domain.model.ProductCategoryInfo
import com.fastcampus.commerce.product.domain.model.ProductInfo
import com.fastcampus.commerce.product.domain.model.SearchProductCondition
import com.fastcampus.commerce.product.domain.service.CategoryReader
import com.fastcampus.commerce.product.domain.service.ProductReader
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class ProductQueryServiceTest : DescribeSpec(
    {
        val productReader = mockk<ProductReader>()
        val categoryReader = mockk<CategoryReader>()
        val productQueryService = ProductQueryService(
            productReader = productReader,
            categoryReader = categoryReader,
        )

        beforeTest {
            clearMocks(productReader, categoryReader)
        }

        describe("카테고리 목록 조회") {
            context("정상 조회") {
                it("전체 카테고리 목록을 조회할 수 있다") {
                    val categoryDetails = listOf(
                        CategoryDetail(
                            groupId = 1L,
                            groupTitle = "cup_size",
                            categoryId = 1L,
                            categoryName = "Small",
                            sortOrder = 1,
                        ),
                        CategoryDetail(
                            groupId = 1L,
                            groupTitle = "cup_size",
                            categoryId = 2L,
                            categoryName = "Large",
                            sortOrder = 2,
                        ),
                        CategoryDetail(
                            groupId = 2L,
                            groupTitle = "intensity",
                            categoryId = 3L,
                            categoryName = "Mild",
                            sortOrder = 1,
                        ),
                        CategoryDetail(
                            groupId = 2L,
                            groupTitle = "intensity",
                            categoryId = 4L,
                            categoryName = "Strong",
                            sortOrder = 2,
                        ),
                    )

                    every { categoryReader.getCategoryDetail() } returns categoryDetails

                    val result = productQueryService.getCategories()

                    result shouldBe listOf(
                        CategoryResponse(groupTitle = "cup_size", id = 1L, name = "Small"),
                        CategoryResponse(groupTitle = "cup_size", id = 2L, name = "Large"),
                        CategoryResponse(groupTitle = "intensity", id = 3L, name = "Mild"),
                        CategoryResponse(groupTitle = "intensity", id = 4L, name = "Strong"),
                    )

                    verify(exactly = 1) { categoryReader.getCategoryDetail() }
                }

                it("카테고리가 없는 경우 빈 리스트를 반환한다") {
                    every { categoryReader.getCategoryDetail() } returns emptyList()

                    val result = productQueryService.getCategories()

                    result shouldBe emptyList()

                    verify(exactly = 1) { categoryReader.getCategoryDetail() }
                }
            }
        }

        describe("상품 목록 조회") {
            val request = SearchProductRequest(
                name = "콜드브루",
                intensityId = 1L,
                cupSizeId = 2L,
            )
            val pageable = PageRequest.of(0, 20)

            context("정상 조회") {
                it("검색 조건으로 상품 목록을 조회할 수 있다") {
                    val productInfos = listOf(
                        ProductInfo(
                            id = 1L,
                            name = "콜드브루",
                            price = 3500,
                            quantity = 100,
                            thumbnail = "https://test.com/thumbnail.png",
                            detailImage = "https://test.com/detail.png",
                            status = SellingStatus.ON_SALE,
                        ),
                        ProductInfo(
                            id = 2L,
                            name = "콜드브루 라떼",
                            price = 4000,
                            quantity = 50,
                            thumbnail = "https://test.com/thumbnail2.png",
                            detailImage = "https://test.com/detail2.png",
                            status = SellingStatus.ON_SALE,
                        ),
                    )
                    val productPage = PageImpl(productInfos, pageable, 2L)
                    val categoryMap = mapOf(
                        1L to ProductCategoryInfo(intensity = "Strong", cupSize = "Large"),
                        2L to ProductCategoryInfo(intensity = "Mild", cupSize = "Medium"),
                    )
                    val expectedCondition = SearchProductCondition(
                        name = "콜드브루",
                        categories = listOf(1L, 2L),
                    )

                    every { productReader.searchProducts(expectedCondition, pageable) } returns productPage
                    every { categoryReader.getProductCategoryMap(listOf(1L, 2L)) } returns categoryMap

                    val result = productQueryService.getProducts(request, pageable)

                    result.content shouldBe listOf(
                        SearchProductResponse(
                            id = 1L,
                            name = "콜드브루",
                            price = 3500,
                            quantity = 100,
                            thumbnail = "https://test.com/thumbnail.png",
                            detailImage = "https://test.com/detail.png",
                            intensity = "Strong",
                            cupSize = "Large",
                        ),
                        SearchProductResponse(
                            id = 2L,
                            name = "콜드브루 라떼",
                            price = 4000,
                            quantity = 50,
                            thumbnail = "https://test.com/thumbnail2.png",
                            detailImage = "https://test.com/detail2.png",
                            intensity = "Mild",
                            cupSize = "Medium",
                        ),
                    )
                    result.totalElements shouldBe 2L
                    result.size shouldBe 20

                    verify(exactly = 1) { productReader.searchProducts(expectedCondition, pageable) }
                    verify(exactly = 1) { categoryReader.getProductCategoryMap(listOf(1L, 2L)) }
                }

                it("빈 검색 조건으로 전체 상품 목록을 조회할 수 있다") {
                    val emptyRequest = SearchProductRequest()
                    val productInfos = listOf(
                        ProductInfo(
                            id = 1L,
                            name = "아메리카노",
                            price = 3000,
                            quantity = 200,
                            thumbnail = "https://test.com/americano.png",
                            detailImage = "https://test.com/americano-detail.png",
                            status = SellingStatus.ON_SALE,
                        ),
                    )
                    val productPage = PageImpl(productInfos, pageable, 1L)
                    val categoryMap = mapOf(
                        1L to ProductCategoryInfo(intensity = "Medium", cupSize = "Small"),
                    )
                    val expectedCondition = SearchProductCondition(
                        name = null,
                        categories = emptyList(),
                    )

                    every { productReader.searchProducts(expectedCondition, pageable) } returns productPage
                    every { categoryReader.getProductCategoryMap(listOf(1L)) } returns categoryMap

                    val result = productQueryService.getProducts(emptyRequest, pageable)

                    result.content shouldBe listOf(
                        SearchProductResponse(
                            id = 1L,
                            name = "아메리카노",
                            price = 3000,
                            quantity = 200,
                            thumbnail = "https://test.com/americano.png",
                            detailImage = "https://test.com/americano-detail.png",
                            intensity = "Medium",
                            cupSize = "Small",
                        ),
                    )
                    result.totalElements shouldBe 1L

                    verify(exactly = 1) { productReader.searchProducts(expectedCondition, pageable) }
                    verify(exactly = 1) { categoryReader.getProductCategoryMap(listOf(1L)) }
                }

                it("카테고리 정보가 없는 상품은 빈 카테고리 정보로 반환한다") {
                    val productInfos = listOf(
                        ProductInfo(
                            id = 1L,
                            name = "테스트 상품",
                            price = 1000,
                            quantity = 10,
                            thumbnail = "https://test.com/test.png",
                            detailImage = "https://test.com/test-detail.png",
                            status = SellingStatus.ON_SALE,
                        ),
                    )
                    val productPage = PageImpl(productInfos, pageable, 1L)
                    val categoryMap = emptyMap<Long, ProductCategoryInfo>()
                    val expectedCondition = SearchProductCondition(
                        name = "콜드브루",
                        categories = listOf(1L, 2L),
                    )

                    every { productReader.searchProducts(expectedCondition, pageable) } returns productPage
                    every { categoryReader.getProductCategoryMap(listOf(1L)) } returns categoryMap

                    val result = productQueryService.getProducts(request, pageable)

                    result.content shouldBe listOf(
                        SearchProductResponse(
                            id = 1L,
                            name = "테스트 상품",
                            price = 1000,
                            quantity = 10,
                            thumbnail = "https://test.com/test.png",
                            detailImage = "https://test.com/test-detail.png",
                            intensity = "",
                            cupSize = "",
                        ),
                    )

                    verify(exactly = 1) { productReader.searchProducts(expectedCondition, pageable) }
                    verify(exactly = 1) { categoryReader.getProductCategoryMap(listOf(1L)) }
                }
            }

            context("빈 결과") {
                it("검색 조건에 해당하는 상품이 없으면 빈 결과를 반환한다") {
                    val emptyPage = PageImpl<ProductInfo>(emptyList(), pageable, 0L)
                    val expectedCondition = SearchProductCondition(
                        name = "콜드브루",
                        categories = listOf(1L, 2L),
                    )

                    every { productReader.searchProducts(expectedCondition, pageable) } returns emptyPage
                    every { categoryReader.getProductCategoryMap(emptyList()) } returns emptyMap()

                    val result = productQueryService.getProducts(request, pageable)

                    result.content shouldBe emptyList()
                    result.totalElements shouldBe 0L

                    verify(exactly = 1) { productReader.searchProducts(expectedCondition, pageable) }
                    verify(exactly = 1) { categoryReader.getProductCategoryMap(emptyList()) }
                }
            }
        }

        describe("상품 상세 조회") {
            context("정상 조회") {
                it("상품 ID로 상품 상세 정보를 조회할 수 있다") {
                    val productId = 1L
                    val productInfo = ProductInfo(
                        id = productId,
                        name = "콜드브루",
                        price = 3500,
                        quantity = 100,
                        thumbnail = "https://test.com/thumbnail.png",
                        detailImage = "https://test.com/detail.png",
                        status = SellingStatus.ON_SALE,
                    )
                    val productCategoryInfo = ProductCategoryInfo(
                        intensity = "Strong",
                        cupSize = "Large",
                    )

                    every { productReader.getProductInfo(productId) } returns productInfo
                    every { categoryReader.getProductCategory(productId) } returns productCategoryInfo

                    val result = productQueryService.getProductDetail(productId)

                    result.id shouldBe productId
                    result.name shouldBe "콜드브루"
                    result.price shouldBe 3500
                    result.quantity shouldBe 100
                    result.thumbnail shouldBe "https://test.com/thumbnail.png"
                    result.detailImage shouldBe "https://test.com/detail.png"
                    result.intensity shouldBe "Strong"
                    result.cupSize shouldBe "Large"

                    verify(exactly = 1) { productReader.getProductInfo(productId) }
                    verify(exactly = 1) { categoryReader.getProductCategory(productId) }
                }

                it("카테고리 정보가 없는 상품도 조회할 수 있다") {
                    val productId = 1L
                    val productInfo = ProductInfo(
                        id = productId,
                        name = "테스트 상품",
                        price = 1000,
                        quantity = 50,
                        thumbnail = "https://test.com/test.png",
                        detailImage = "https://test.com/test-detail.png",
                        status = SellingStatus.ON_SALE,
                    )
                    val emptyProductCategoryInfo = ProductCategoryInfo.empty()

                    every { productReader.getProductInfo(productId) } returns productInfo
                    every { categoryReader.getProductCategory(productId) } returns emptyProductCategoryInfo

                    val result = productQueryService.getProductDetail(productId)

                    result.id shouldBe productId
                    result.name shouldBe "테스트 상품"
                    result.price shouldBe 1000
                    result.quantity shouldBe 50
                    result.thumbnail shouldBe "https://test.com/test.png"
                    result.detailImage shouldBe "https://test.com/test-detail.png"
                    result.intensity shouldBe ""
                    result.cupSize shouldBe ""

                    verify(exactly = 1) { productReader.getProductInfo(productId) }
                    verify(exactly = 1) { categoryReader.getProductCategory(productId) }
                }
            }
        }
    },
)
