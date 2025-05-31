package com.fastcampus.commerce.admin.product.interfaces

import com.fastcampus.commerce.restdoc.documentation
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
class AdminProductControllerRestDocTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) : DescribeSpec() {
        override fun extensions() = listOf(SpringExtension)

        init {
            beforeSpec {
                RestAssuredMockMvc.mockMvc(mockMvc)
            }

            val tag = "Admin-Product"
            val privateResource = true

            describe("GET /admin/products/selling-status - 상품 판매상태 목록 조회") {
                it("상품 판매상태 목록을 조회할 수 있다.") {
                    documentation(
                        identifier = "상품 판매상태 코드 목록 조회",
                        tag = tag,
                        summary = "상품 판매상태 코드 목록을 조회한다.",
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.GET, "/admin/products/selling-status")

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        responseBody {
                            field("data.status[0].code", "판매상태 코드", "ON_SALE")
                            field("data.status[0].label", "판매상태", "판매중")
                            ignoredField("error")
                        }
                    }
                }
            }

            describe("POST /admin/products - 상품 등록") {

                val summary = "관리자가 상품을 등록한다."

                it("상품을 등록할 수 있다.") {
                    documentation(
                        identifier = "성공",
                        tag = tag,
                        summary = summary,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.POST, "/admin/products")

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        requestBody {
                            field("name", "상품명", "스타벅스")
                            field("price", "가격", 10000)
                            field("quantity", "재고", 10)
                            field("thumbnail", "썸네일 url", "https://example.com/thumbnail.jpg")
                            field("detailImage", "상세 이미지 url", "https://example.com/detail.jpg")
                            field("intensityId", "강도 카테고리 아이디", 1)
                            field("cupSizeId", "컵사이즈 카테고리 아이디", 10)
                        }

                        responseBody {
                            field("data.id", "등록된 상품 아이디", 1)
                            ignoredField("error")
                        }
                    }
                }
                it("상품 등록 요청시 상품명이 비어있으면 예외가 발생한다.") {
                    documentation(
                        identifier = "실패 - 상품명",
                        tag = tag,
                        summary = summary,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.POST, "/admin/products")

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        requestBody {
                            field("name", "상품명", "")
                            field("price", "가격", 10000)
                            field("quantity", "재고", 10)
                            field("thumbnail", "썸네일 url", "https://example.com/thumbnail.jpg")
                            field("detailImage", "상세 이미지 url", "https://example.com/detail.jpg")
                            field("intensityId", "강도 카테고리 아이디", 1)
                            field("cupSizeId", "컵사이즈 카테고리 아이디", 10)
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", "APD-001")
                            field("error.message", "에러 메시지", "Product Name is Empty.")
                        }
                    }
                }
            }
            describe("PUT /products/{productId} - 상품 수정") {
                it("상품 정보를 수정할 수 있다.") {
                    documentation(
                        identifier = "상품 수정",
                        summary = "관리자가 상품을 수정한다.",
                        privateResource = true,
                        tag = "Admin-Product",
                    ) {
                        requestLine(HttpMethod.PUT, "/admin/products/{productId}") {
                            pathVariable("productId", "상품 아이디", 1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        requestBody {
                            field("name", "상품명", "스타벅스")
                            field("price", "가격", 10000)
                            field("quantity", "재고", 10)
                            field("thumbnail", "썸네일 url", "https://example.com/thumbnail.jpg")
                            field("detailImage", "상세 이미지 url", "https://example.com/detail.jpg")
                            field("intensityId", "강도 카테고리 아이디", 1)
                            field("cupSizeId", "컵사이즈 카테고리 아이디", 10)
                        }

                        responseHeaders {
                            header(HttpHeaders.CONTENT_TYPE, "Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        }

                        responseBody {
                            field("data.id", "수정된 상품 아이디", 1)
                            ignoredField("error")
                        }
                    }
                }
            }
        }
    }
