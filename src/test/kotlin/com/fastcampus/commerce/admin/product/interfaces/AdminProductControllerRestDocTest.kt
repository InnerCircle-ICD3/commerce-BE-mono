package com.fastcampus.commerce.admin.product.interfaces

import com.fastcampus.commerce.admin.product.application.AdminProductService
import com.fastcampus.commerce.admin.product.application.response.AdminProductDetailResponse
import com.fastcampus.commerce.admin.product.application.response.SearchAdminProductResponse
import com.fastcampus.commerce.admin.product.application.response.SellingStatusResponse
import com.fastcampus.commerce.admin.product.interfaces.request.RegisterProductApiRequest
import com.fastcampus.commerce.admin.product.interfaces.request.UpdateProductApiRequest
import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.restdoc.documentation
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(AdminProductController::class)
@Import(TestConfig::class)
class AdminProductControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminProductService: AdminProductService

    val tag = "Admin-Product"
    val privateResource = true

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("GET /admin/products/selling-status - 상품 판매상태 목록 조회") {
            val summary = "상품 판매상태 목록을 조회할 수 있다."
            it("상품 판매상태 목록을 조회할 수 있다.") {
                every { adminProductService.getSellingStatus() } returns listOf(SellingStatusResponse("ON_SALE", "판매중"))

                documentation(
                    identifier = "상품_판매상태_조회_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.GET, "/admin/products/selling-status")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    responseBody {
                        field("data[0].code", "판매상태 코드", "ON_SALE")
                        field("data[0].label", "판매상태", "판매중")
                        ignoredField("error")
                    }
                }
            }
        }

        describe("GET /admin/products - 상품 목록 조회") {
            val summary = "상품 목록을 조회할 수 있다."
            it("상품 목록을 조회할 수 있다.") {
                val searchProductResponses = listOf(
                    SearchAdminProductResponse(
                        id = 1L,
                        name = "콜드브루",
                        price = 3500,
                        quantity = 100,
                        thumbnail = "https://test.com/thumbnail.png",
                        intensity = "Strong",
                        cupSize = "Large",
                        status = SellingStatus.ON_SALE,
                    ),
                )
                val response = PageImpl(searchProductResponses, PageRequest.of(0, 10), 1L)
                every { adminProductService.searchProducts(any(), any()) } returns response

                documentation(
                    identifier = "관리자_상품_목록_조회_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.GET, "/admin/products")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    queryParameters {
                        optionalField("name", "상품명", "콜드")
                        optionalField("intensityId", "원두 강도 카테고리 ID", 1)
                        optionalField("cupSizeId", "컵 사이즈 카테고리 ID", 2)
                        optionalField("status", "판매상태(ON_SALE, UNAVAILABLE, HIDDEN)", SellingStatus.ON_SALE)
                        optionalField("page", "페이지 번호 (1부터 시작, 기본값: 1)", 1)
                    }

                    responseBody {
                        field("data.content[0].id", "상품 ID", searchProductResponses[0].id.toInt())
                        field("data.content[0].name", "상품명", searchProductResponses[0].name)
                        field("data.content[0].price", "가격", searchProductResponses[0].price)
                        field("data.content[0].thumbnail", "썸네일 이미지 URL", searchProductResponses[0].thumbnail)
                        field("data.content[0].status", "판매상태", searchProductResponses[0].status.name)
                        field("data.content[0].quantity", "재고", searchProductResponses[0].quantity)
                        field("data.content[0].intensity", "원두 강도", searchProductResponses[0].intensity)
                        field("data.content[0].cupSize", "컵 사이즈", searchProductResponses[0].cupSize)
                        field("data.page", "현재 페이지 번호", response.number + 1)
                        field("data.size", "페이지 크기", response.size)
                        field("data.totalPages", "전체 페이지 수", response.totalPages)
                        field("data.totalElements", "총 상품 수", response.totalElements.toInt())
                        ignoredField("error")
                    }
                }
            }
        }

        describe("GET /admin/products/{productId} - 상품 상세 조회") {
            val summary = "상품 상세 정보를 조회할 수 있다."
            val description = """
                PRO-001: 상품을 찾을 수 없습니다.
                PRO-002: 상품 재고를 찾을 수 없습니다.
            """.trimMargin()

            it("상품 ID로 상품 상세 정보를 조회할 수 있다.") {
                val productId = 1L
                val productDetailResponse = AdminProductDetailResponse(
                    id = productId,
                    name = "콜드브루",
                    price = 3500,
                    quantity = 100,
                    thumbnail = "https://test.com/thumbnail.png",
                    detailImage = "https://test.com/detail.png",
                    intensity = "Strong",
                    cupSize = "Large",
                    status = SellingStatus.ON_SALE,
                )

                every {
                    adminProductService.getProduct(productId)
                } returns productDetailResponse

                documentation(
                    identifier = "관리자_상품_상세_조회_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                ) {
                    requestLine(HttpMethod.GET, "/admin/products/{productId}") {
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
                        field("data.status", "판매상태", productDetailResponse.status.name)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("POST /admin/products - 상품 등록") {
            val summary = "상품을 등록할 수 있다."
            val description = """
                PRD-101: 상품명이 비어있습니다.
                PRD-102: 상품명이 너무 깁니다.
                PRD-103: 가격은 양수여야합니다.
                PRD-104: 재고는 음수일 수 없습니다.
                PRD-105: 유효하지 않은 카테고리입니다.
            """.trimMargin()

            it("상품을 등록할 수 있다.") {
                val request = RegisterProductApiRequest(
                    name = "콜드브루",
                    price = 3500,
                    quantity = 100,
                    thumbnail = "https://test.com/thumbnail.png",
                    detailImage = "https://test.com/detailImage.png",
                    intensityId = 1L,
                    cupSizeId = 10L,
                )

                every { adminProductService.register(any<Long>(), request.toServiceRequest()) } returns 10L

                documentation(
                    identifier = "상품_등록_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.POST, "/admin/products")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("name", "상품명", request.name)
                        field("price", "가격", request.price)
                        field("quantity", "재고 수량", request.quantity)
                        field("thumbnail", "썸네일 이미지", request.thumbnail)
                        field("detailImage", "상세 이미지", request.detailImage)
                        field("intensityId", "강도 카테고리 아이디", request.intensityId)
                        field("cupSizeId", "컵사이즈 카테고리 아이디", request.cupSizeId)
                    }

                    responseBody {
                        field("data.productId", "생성된 상품 아이디", 10)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("PUT /admin/products/{productId} - 상품 수정") {
            val summary = "상품을 수정할 수 있다."
            val description = """
                PRO-001: 상품을 찾을 수 없습니다.
                PRO-002: 상품 재고를 찾을 수 없습니다.
                PRD-101: 상품명이 비어있습니다.
                PRD-102: 상품명이 너무 깁니다.
                PRD-103: 가격은 양수여야합니다.
                PRD-104: 재고는 음수일 수 없습니다.
                PRD-105: 유효하지 않은 카테고리입니다.
            """.trimMargin()

            it("상품을 수정할 수 있다.") {
                val productId = 10L
                val request = UpdateProductApiRequest(
                    name = "콜드브루",
                    price = 3500,
                    quantity = 100,
                    thumbnail = "https://test.com/thumbnail.png",
                    detailImage = "https://test.com/detailImage.png",
                    intensityId = 1L,
                    cupSizeId = 10L,
                    status = SellingStatus.UNAVAILABLE,
                )

                every { adminProductService.update(any<Long>(), request.toServiceRequest(productId)) } just Runs

                documentation(
                    identifier = "상품_수정_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.PUT, "/admin/products/{productId}") {
                        pathVariable("productId", "상품 아이디", productId)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("name", "상품명", request.name)
                        field("price", "가격", request.price)
                        field("quantity", "재고 수량", request.quantity)
                        field("thumbnail", "썸네일 이미지", request.thumbnail)
                        field("detailImage", "상세 이미지", request.detailImage)
                        field("intensityId", "강도 카테고리 아이디", request.intensityId)
                        field("cupSizeId", "컵사이즈 카테고리 아이디", request.cupSizeId)
                        field("status", "판매상태 코드", request.status)
                    }

                    responseBody {
                        field("data.productId", "수정된 상품 아이디", productId.toInt())
                        ignoredField("error")
                    }
                }
            }
        }
        describe("DELETE /admin/products/{productId} - 상품 삭제") {
            val summary = "상품을 삭제할 수 있다."
            val description = """
                PRO-001: 상품을 찾을 수 없습니다.
                PRO-002: 상품 재고를 찾을 수 없습니다.
            """.trimMargin()

            it("상품을 삭제할 수 있다.") {
                val productId = 10L
                every { adminProductService.delete(any<Long>(), productId) } just Runs

                documentation(
                    identifier = "상품_삭제_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.DELETE, "/admin/products/{productId}") {
                        pathVariable("productId", "상품 아이디", productId)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    responseBody {
                        field("data.message", "응답 메시지", "OK")
                        ignoredField("error")
                    }
                }
            }
        }
    }
}
