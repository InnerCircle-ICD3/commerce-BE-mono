package com.fastcampus.commerce.admin.product.interfaces

import com.fastcampus.commerce.admin.product.domain.error.AdminProductErrorCode
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
                        identifier = "판매상태 목록 조회",
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

            describe("GET /admin/products - 상품 검색") {
                val summary = "관리자가 상품을 검색한다."

                it("상품을 검색할 수 있다.") {
                    documentation(
                        identifier = "관리자_상품검색_성공",
                        tag = tag,
                        summary = summary,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.GET, "/admin/products")

                        queryParameters {
                            optionalField("name", "상품명", "스타벅스")
                            optionalField("intensityId", "강도 카테고리 아이디", 1)
                            optionalField("cupSizeId", "컵사이즈 카테고리 아이디", 10)
                            optionalField("sellingStatusCode", "판매상태 코드", "ON_SALE")
                        }

                        responseBody {
                            field("data.content[0].id", "상품 아이디", 1)
                            field("data.content[0].name", "상품명", "스타벅스")
                            field("data.content[0].price", "가격", 10000)
                            field("data.content[0].quantity", "재고", 10)
                            field("data.content[0].intensity", "강도 카테고리 라벨", "1")
                            field("data.content[0].cupSize", "컵사이즈 카테고리 라벨", "25ml")
                            field("data.content[0].sellingStatus", "판매상태 라벨", "판매중")
                            field("data.page", "현재 페이지 (기본값 1)", 1)
                            field("data.size", "페이지 사이즈(기본값 10)", 10)
                            field("data.totalPages", "전체 페이지 수", 10)
                            field("data.totalElements", "총 수", 99)
                            ignoredField("error")
                        }
                    }
                }
            }
            describe("GET /admin/products/{productId} - 상품 단건 조회") {
                val summary = "관리자가 상품 상세정보를 조회한다."
                val description = """
                APD-008: 존재하지 않는 상품입니다.
                """.trimMargin()

                it("상품 정보를 조회할 수 있다.") {
                    documentation(
                        identifier = "관리자 상품조회 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.GET, "/admin/products/{productId}") {
                            pathVariable("productId", "상품 아이디", 1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        responseBody {
                            field("data.id", "상품아이이디", "1")
                            field("data.name", "상품명", "스타벅스")
                            field("data.price", "가격", 10000)
                            field("data.quantity", "재고", 10)
                            field("data.thumbnail", "썸네일 url", "https://example.com/thumbnail.jpg")
                            field("data.detailImage", "상세 이미지 url", "https://example.com/detail.jpg")
                            field("data.intensityId", "강도 카테고리 아이디", 1)
                            field("data.cupSizeId", "컵사이즈 카테고리 아이디", 10)
                            field("data.sellingStatusCode", "판매상태 코드", "ON_SALE")
                            ignoredField("error")
                        }
                    }
                }
                it("존재하지 않은 상품을 조회하려고 하면 예외가 발생한다.") {
                    documentation(
                        identifier = "관리자 상품조회 실패",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.GET, "/admin/products/{productId}") {
                            pathVariable("productId", "상품 아이디", -1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", AdminProductErrorCode.PRODUCT_NOT_EXISTS.code)
                            field("error.message", "에러 메시지", AdminProductErrorCode.PRODUCT_NOT_EXISTS.message)
                        }
                    }
                }
            }

            describe("POST /admin/products - 상품 등록") {

                val summary = "관리자가 상품을 등록한다."
                val description = """
                    APD-001: 상품명이 비어있습니다.
                    APD-002: 상품명은 최대 255자까지 입력가능합니다.
                    APD-003: 상품가격은 0보다 커야합니다.
                    APD-004: 상품재고는 0이상이어야합니다.
                    APD-005: 상품 썸네일이 비어있습니다.
                    APD-006: 상품 상세이미지가 비어있습니다.
                    APD-007: 유효하지 않은 카테고리(강도/컵사이즈)입니다.
                """.trimMargin()

                it("상품을 등록할 수 있다.") {
                    documentation(
                        identifier = "관리자 상품등록 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
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
                        identifier = "관리자 상품등록 실패",
                        tag = tag,
                        summary = summary,
                        description = description,
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
                            field("error.code", "에러 코드", AdminProductErrorCode.PRODUCT_NAME_EMPTY.code)
                            field("error.message", "에러 메시지", AdminProductErrorCode.PRODUCT_NAME_EMPTY.message)
                        }
                    }
                }
            }
            describe("PUT /products/{productId} - 상품 수정") {
                val summary = "관리자가 상품을 수정한다."
                val description = """
                    APD-001: 상품명이 비어있습니다.
                    APD-002: 상품명은 최대 255자까지 입력가능합니다.
                    APD-003: 상품가격은 0보다 커야합니다.
                    APD-004: 상품재고는 0이상이어야합니다.
                    APD-005: 상품 썸네일이 비어있습니다.
                    APD-006: 상품 상세이미지가 비어있습니다.
                    APD-007: 유효하지 않은 카테고리(강도/컵사이즈)입니다.
                    APD-008: 존재하지 않는 상품입니다.
                """.trimMargin()

                it("상품 정보를 수정할 수 있다.") {
                    documentation(
                        identifier = "관리자 상품수정 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
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

                        responseBody {
                            field("data.id", "수정된 상품 아이디", 1)
                            ignoredField("error")
                        }
                    }
                }
                it("존재하지 않은 상품을 수정하려고 하면 예외가 발생한다.") {
                    documentation(
                        identifier = "관리자 상품수정 실패",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.PUT, "/admin/products/{productId}") {
                            pathVariable("productId", "상품 아이디", -1)
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

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", AdminProductErrorCode.PRODUCT_NOT_EXISTS.code)
                            field("error.message", "에러 메시지", AdminProductErrorCode.PRODUCT_NOT_EXISTS.message)
                        }
                    }
                }
            }
            describe("DELETE /products/{productId} - 상품 삭제") {
                val summary = "관리자가 상품을 삭제한다."
                val description = """
                    APD-008: 존재하지 않는 상품입니다.
                """.trimMargin()

                it("상품을 삭제할 수 있다.") {
                    documentation(
                        identifier = "관리자 상품삭제 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.DELETE, "/admin/products/{productId}") {
                            pathVariable("productId", "상품 아이디", 1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        responseBody {
                            field("data.message", "삭제 메시지", "OK")
                            ignoredField("error")
                        }
                    }
                }
                it("존재하지 않은 상품을 삭제하려고 하면 예외가 발생한다.") {
                    documentation(
                        identifier = "관리자 상품삭제  실패",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.DELETE, "/admin/products/{productId}") {
                            pathVariable("productId", "상품 아이디", -1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", AdminProductErrorCode.PRODUCT_NOT_EXISTS.code)
                            field("error.message", "에러 메시지", AdminProductErrorCode.PRODUCT_NOT_EXISTS.message)
                        }
                    }
                }
            }
        }
    }
