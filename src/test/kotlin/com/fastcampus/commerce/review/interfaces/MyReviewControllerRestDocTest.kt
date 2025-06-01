package com.fastcampus.commerce.review.interfaces

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
class MyReviewControllerRestDocTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) : DescribeSpec() {
        override fun extensions() = listOf(SpringExtension)

        init {
            beforeSpec {
                RestAssuredMockMvc.mockMvc(mockMvc)
            }

            var tag = "Review"

            describe("GET /users/me/reviews - 본인이 작성한 리뷰 목록") {
                val summary = "본인이 작성한 리뷰 목록을 조회한다."
                val description = """

                """.trimMargin()

                it("로그인한 사용자가 작성한 리뷰를 조회할 수 있다.") {
                    documentation(
                        identifier = "작성한 리뷰목록 조회 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                    ) {
                        requestLine(HttpMethod.GET, "/users/me/reviews")

                        queryParameters {
                            optionalField("monthRange", "검색 범위(단위: month)", 3)
                            optionalField("page", "페이지", 1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        responseBody {
                            field("data.content[0].reviewId", "리뷰 아이디", 2)
                            field("data.content[0].rating", "별점", 3)
                            field("data.content[0].content", "리뷰 내용", "배송이 느렸어요 ㅠ")
                            field("data.content[0].createdAt", "리뷰 작성일", "2025-06-01T11:45:12")
                            optionalField("data.content[0].adminReply.content", "관리자의 리뷰 답글 내용", "죄송합니다.")
                            optionalField("data.content[0].adminReply.createdAt", "관리자의 리뷰 답글 작성일", "2025-06-01T11:45:12")
                            field("data.content[0].productId", "리뷰 상품아이디", 3)
                            field("data.content[0].productName", "리뷰 상품명", "할리스 캡슐")
                            field("data.content[0].productThumbnail", "리뷰 상품 썸네일", "https://example.com/thumbnail2.jpg")
                            field("data.page", "현재 페이지 (기본값 1)", 1)
                            field("data.size", "페이지 사이즈(기본값 10)", 10)
                            field("data.totalPages", "전체 페이지 수", 10)
                            field("data.totalElements", "총 수", 99)
                            ignoredField("error")
                        }
                    }
                }
            }
        }
    }
