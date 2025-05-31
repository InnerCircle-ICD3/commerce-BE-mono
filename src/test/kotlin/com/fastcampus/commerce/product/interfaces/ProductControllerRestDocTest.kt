package com.fastcampus.commerce.product.interfaces

import com.fastcampus.commerce.restdoc.documentation
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
class ProductControllerRestDocTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) : DescribeSpec() {
        override fun extensions() = listOf(SpringExtension)

        init {
            beforeSpec {
                RestAssuredMockMvc.mockMvc(mockMvc)
            }

            describe("GET /products/categories - 상품 카테고리 목록 조회") {
                it("상품 카테고리 목록을 조회할 수 있다.") {
                    documentation(
                        identifier = "상품 카테고리 목록 조회",
                        summary = "상품 카테고리 목록을 조회한다.",
                        privateResource = false,
                        tag = "Product-Common",
                    ) {
                        requestLine(HttpMethod.GET, "/products/categories")

                        responseBody {
                            field("data.intensities[0].id", "강도 카테고리 아이디", 1)
                            field("data.intensities[0].label", "강도 카테고리명", "1")
                            field("data.cupSizes[0].id", "컵사이즈 카테고리 아이디", 10)
                            field("data.cupSizes[0].label", "컵사이즈 카테고리명", "25ml")
                            ignoredField("error")
                        }
                    }
                }
            }

            describe("GET /products - 상품 검색") {
                it("상품을 검색할 수 있다.") {
                    documentation(
                        identifier = "상품 검색",
                        summary = "상품을 검색한다.",
                        privateResource = false,
                        tag = "Product",
                    ) {
                        requestLine(HttpMethod.GET, "/products")

                        queryParameters {
                            optionalField("name", "상품명", "스타벅스")
                            optionalField("intensityId", "강도 카테고리 아이디", 1)
                            optionalField("cupSizeId", "컵사이즈 카테고리 아이디", 10)
                        }

                        responseBody {
                            field("data.content[0].id", "상품 아이디", 1)
                            field("data.content[0].name", "상품명", "스타벅스")
                            field("data.content[0].price", "가격", 10000)
                            field("data.content[0].quantity", "재고", 10)
                            field("data.content[0].thumbnail", "썸네일 url", "https://example.com/thumbnail.jpg")
                            field("data.content[0].intensity", "강도 카테고리 라벨", "1")
                            field("data.content[0].cupSize", "컵사이즈 카테고리 라벨", "25ml")
                            field("data.content[0].isSoldOut", "품절 여부", false)
                            field("data.page", "현재 페이지 (기본값 1)", 1)
                            field("data.size", "페이지 사이즈(기본값 10)", 10)
                            field("data.totalPages", "전체 페이지 수", 10)
                            field("data.totalElements", "총 수", 99)
                            ignoredField("error")
                        }
                    }
                }
            }
            describe("GET /product/{productId} - 상품 상세 조회") {
                it("상품 아이디로 상품 상세정보를 조회할 수 있다.") {
                    documentation(
                        identifier = "상품 상세 조회",
                        summary = "상품 아이디로 상품 상세정보를 조회한다.",
                        privateResource = false,
                        tag = "Product",
                    ) {
                        requestLine(HttpMethod.GET, "/products/{productId}") {
                            pathVariable("productId", "상품 아이디", 1)
                        }

                        responseBody {
                            field("data.id", "상품 아이디", 1)
                            field("data.name", "상품명", "스타벅스")
                            field("data.price", "가격", 10000)
                            field("data.quantity", "재고", 10)
                            field("data.thumbnail", "썸네일 url", "https://example.com/thumbnail.jpg")
                            field("data.detailImage", "상세 이미지 url", "https://example.com/detail.jpg")
                            field("data.intensity", "강도 카테고리 라벨", "1")
                            field("data.cupSize", "컵사이즈 카테고리 라벨", "25ml")
                            field("data.isSoldOut", "품절 여부", false)
                            ignoredField("error")
                        }
                    }
                }
                it("유효하지 않은 상품 아이디로 상품을 조회하면 예외가 발생한다.") {
                    documentation(
                        identifier = "상품 상세 조회",
                        summary = "상품 아이디로 상품 상세정보를 조회한다.",
                        privateResource = false,
                        tag = "Product",
                    ) {
                        requestLine(HttpMethod.GET, "/products/{productId}") {
                            pathVariable("productId", "상품 아이디", 2)
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", "PRO-001")
                            field("error.message", "에러 메시지", "Product Not Found")
                        }
                    }
                }
            }
        }
    }
