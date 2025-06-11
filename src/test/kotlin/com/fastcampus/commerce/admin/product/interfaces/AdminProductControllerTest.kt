package com.fastcampus.commerce.admin.product.interfaces

import com.fastcampus.commerce.admin.product.application.AdminProductService
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
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(AdminProductController::class)
@Import(TestConfig::class)
class AdminProductControllerTest : DescribeSpec() {
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

        describe("POST /admin/products - 상품 등록") {
            val summary = "상품을 등록할 수 있다."

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

            it("상품을 삭제할 수 있다.") {
                val productId = 10L
                every { adminProductService.delete(any<Long>(), productId) } just Runs

                documentation(
                    identifier = "상품_삭제_성공",
                    tag = tag,
                    summary = summary,
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
