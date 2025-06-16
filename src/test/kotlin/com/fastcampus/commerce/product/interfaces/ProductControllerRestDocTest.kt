package com.fastcampus.commerce.product.interfaces

import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.product.application.MainProductService
import com.fastcampus.commerce.product.application.ProductQueryService
import com.fastcampus.commerce.product.application.response.CategoryResponse
import com.fastcampus.commerce.product.application.response.ProductDetailResponse
import com.fastcampus.commerce.product.application.response.SearchProductResponse
import com.fastcampus.commerce.restdoc.documentation
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(ProductController::class)
@Import(TestConfig::class)
class ProductControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var productQueryService: ProductQueryService

    @MockkBean
    lateinit var mainProductService: MainProductService

    val tag = "Product"

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("GET /products/categories - 카테고리 목록 조회") {
            val summary = "상품 카테고리 목록을 조회할 수 있다."

            it("전체 카테고리 목록을 조회할 수 있다.") {
                val categoryResponses = listOf(
                    CategoryResponse(groupTitle = "cup_size", id = 1L, name = "25ml"),
                    CategoryResponse(groupTitle = "cup_size", id = 2L, name = "80ml"),
                    CategoryResponse(groupTitle = "intensity", id = 3L, name = "1"),
                    CategoryResponse(groupTitle = "intensity", id = 4L, name = "2"),
                )

                every {
                    productQueryService.getCategories()
                } returns categoryResponses

                documentation(
                    identifier = "카테고리_목록_조회_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/products/categories")

                    responseBody {
                        field(
                            "data.cupSizes",
                            "컵 사이즈 목록",
                            listOf(
                                mapOf("id" to "1", "label" to "25ml"),
                                mapOf("id" to "2", "label" to "80ml"),
                            ),
                        )
                        field("data.cupSizes[0].id", "카테고리 ID", "1")
                        field("data.cupSizes[0].label", "카테고리명", "25ml")
                        field(
                            "data.intensities",
                            "원두 강도 목록",
                            listOf(
                                mapOf("id" to "3", "label" to "1"),
                                mapOf("id" to "4", "label" to "2"),
                            ),
                        )
                        field("data.intensities[0].id", "카테고리 ID", "3")
                        field("data.intensities[0].label", "카테고리명", "1")
                        ignoredField("error")
                    }
                }
            }
        }

        describe("GET /products - 상품 목록 조회") {
            val summary = "상품 목록을 조회할 수 있다."

            it("전체 상품 목록을 조회할 수 있다.") {
                val searchProductResponses = listOf(
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
                        name = "아메리카노",
                        price = 3000,
                        quantity = 150,
                        thumbnail = "https://test.com/americano.png",
                        detailImage = "https://test.com/americano-detail.png",
                        intensity = "Medium",
                        cupSize = "Small",
                    ),
                )
                val response = PageImpl(searchProductResponses, PageRequest.of(0, 20), 2L)

                every {
                    productQueryService.getProducts(any(), any())
                } returns response

                documentation(
                    identifier = "상품_목록_조회_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/products")

                    queryParameters {
                        optionalField("name", "상품명 (부분 검색)", "콜드브루")
                        optionalField("intensityId", "원두 강도 카테고리 ID", 1)
                        optionalField("cupSizeId", "컵 사이즈 카테고리 ID", 2)
                        optionalField("page", "페이지 번호 (1부터 시작, 기본값: 1)", 1)
                        optionalField("size", "페이지 크기 (기본값: 10, 최대: 50)", 10)
                    }

                    responseBody {
                        field("data.content[0].id", "상품 ID", searchProductResponses[0].id.toInt())
                        field("data.content[0].name", "상품명", searchProductResponses[0].name)
                        field("data.content[0].price", "가격", searchProductResponses[0].price)
                        field("data.content[0].quantity", "재고 수량", searchProductResponses[0].quantity)
                        field("data.content[0].thumbnail", "썸네일 이미지 URL", searchProductResponses[0].thumbnail)
                        field("data.content[0].detailImage", "상세 이미지 URL", searchProductResponses[0].detailImage)
                        field("data.content[0].intensity", "원두 강도", searchProductResponses[0].intensity)
                        field("data.content[0].cupSize", "컵 사이즈", searchProductResponses[0].cupSize)
                        field("data.content[0].isSoldOut", "품절 여부", false)
                        field("data.page", "현재 페이지 번호", response.number)
                        field("data.size", "페이지 크기", response.size)
                        field("data.totalPages", "전체 페이지 수", response.totalPages)
                        field("data.totalElements", "총 상품 수", response.totalElements.toInt())
                        ignoredField("error")
                    }
                }
            }

            it("검색 결과가 없을 때 빈 결과를 반환한다.") {
                val response = PageImpl<SearchProductResponse>(emptyList(), PageRequest.of(0, 20), 0L)

                every {
                    productQueryService.getProducts(any(), any())
                } returns response

                documentation(
                    identifier = "상품_검색_결과_없음",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/products")

                    queryParameters {
                        field("name", "존재하지 않는 상품명", "존재하지않는상품")
                    }

                    responseBody {
                        field("data.content", "상품 목록", emptyList<Any>())
                        field("data.page", "현재 페이지 번호", response.number)
                        field("data.size", "페이지 크기", response.size)
                        field("data.totalPages", "전체 페이지 수", response.totalPages)
                        field("data.totalElements", "총 상품 수", response.totalElements.toInt())
                        ignoredField("error")
                    }
                }
            }
        }

        describe("GET /products/{productId} - 상품 상세 조회") {
            val summary = "상품 상세 정보를 조회할 수 있다."
            val description = """
                PRO-001: 상품을 찾을 수 없습니다.
                PRO-002: 상품 재고를 찾을 수 없습니다.
            """.trimMargin()

            it("상품 ID로 상품 상세 정보를 조회할 수 있다.") {
                val productId = 1L
                val productDetailResponse = ProductDetailResponse(
                    id = productId,
                    name = "콜드브루",
                    price = 3500,
                    quantity = 100,
                    thumbnail = "https://test.com/thumbnail.png",
                    detailImage = "https://test.com/detail.png",
                    intensity = "Strong",
                    cupSize = "Large",
                )

                every {
                    productQueryService.getProductDetail(productId)
                } returns productDetailResponse

                documentation(
                    identifier = "상품_상세_조회_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                ) {
                    requestLine(HttpMethod.GET, "/products/{productId}") {
                        pathVariable("productId", "상품 ID", productId)
                    }

                    responseBody {
                        field("data.id", "상품 ID", productDetailResponse.id.toInt())
                        field("data.name", "상품명", productDetailResponse.name)
                        field("data.price", "가격", productDetailResponse.price)
                        field("data.quantity", "재고 수량", productDetailResponse.quantity)
                        field("data.thumbnail", "썸네일 이미지 URL", productDetailResponse.thumbnail)
                        field("data.detailImage", "상세 이미지 URL", productDetailResponse.detailImage)
                        field("data.intensity", "원두 강도", productDetailResponse.intensity)
                        field("data.cupSize", "컵 사이즈", productDetailResponse.cupSize)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("GET /products/main - 메인 전시상품 조회") {
            val summary = "메인 상품 목록을 조회할 수 있다."

            it("메인 상품 목록을 조회할 수 있다.") {
                val element = SearchProductResponse(
                    id = 1L,
                    name = "콜드브루",
                    price = 3500,
                    quantity = 100,
                    thumbnail = "https://test.com/thumbnail.png",
                    detailImage = "https://test.com/detail.png",
                    intensity = "Strong",
                    cupSize = "Large",
                )

                every { mainProductService.getNewProducts() } returns listOf(element)
                every { mainProductService.getBestProducts() } returns listOf(element)

                documentation(
                    identifier = "메인_상품목록_조회",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/products/main")

                    responseBody {
                        field("data.new[0].id", "상품 ID", element.id.toInt())
                        field("data.new[0].name", "상품명", element.name)
                        field("data.new[0].price", "가격", element.price)
                        field("data.new[0].quantity", "재고 수량", element.quantity)
                        field("data.new[0].thumbnail", "썸네일 이미지 URL", element.thumbnail)
                        field("data.new[0].detailImage", "상세 이미지 URL", element.detailImage)
                        field("data.new[0].intensity", "원두 강도", element.intensity)
                        field("data.new[0].cupSize", "컵 사이즈", element.cupSize)
                        field("data.new[0].isSoldOut", "품절 여부", false)
                        field("data.best[0].id", "상품 ID", element.id.toInt())
                        field("data.best[0].name", "상품명", element.name)
                        field("data.best[0].price", "가격", element.price)
                        field("data.best[0].quantity", "재고 수량", element.quantity)
                        field("data.best[0].thumbnail", "썸네일 이미지 URL", element.thumbnail)
                        field("data.best[0].detailImage", "상세 이미지 URL", element.detailImage)
                        field("data.best[0].intensity", "원두 강도", element.intensity)
                        field("data.best[0].cupSize", "컵 사이즈", element.cupSize)
                        field("data.best[0].isSoldOut", "품절 여부", false)
                        ignoredField("error")
                    }
                }
            }
        }
    }
}
