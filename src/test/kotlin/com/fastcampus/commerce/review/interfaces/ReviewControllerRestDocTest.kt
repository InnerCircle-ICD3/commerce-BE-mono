package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.restdoc.documentation
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
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
class ReviewControllerRestDocTest
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

            describe("POST /reviews - 리뷰 등록") {
                val summary = "리뷰를 등록한다."
                val description = """
                RVW-001: 리뷰 내용을 입력해주세요.
                RVW-002: 별점은 1~5점사이로 선택해주세요.
                RVW-003: 배송완료된 주문건에 대해서만 리뷰를 작성할 수 있습니다.
                RVW-004: 배송완료 후 30일 이내의 주문건에 대해서만 리뷰를 작성 및 수정할 수 있습니다.
                """.trimMargin()

                it("리뷰를 등록할 수 있다.") {
                    documentation(
                        identifier = "리뷰등록 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                    ) {
                        requestLine(HttpMethod.POST, "/reviews")

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                            header(HttpHeaders.CONTENT_TYPE, "Content-Type", "application/json")
                        }

                        requestBody {
                            field("orderNumber", "주문 번호", "ORD20250601123456789")
                            field("orderItemId", "주문 아이템 아이디", 1)
                            field("rating", "별점", 4)
                            field("content", "리뷰내용", "맛있어요.")
                        }

                        responseBody {
                            field("data.reviewId", "생성된 리뷰 아이디", 1)
                            ignoredField("error")
                        }
                    }
                }
                it("빈 리뷰내용으로 리뷰를 등록하려고하면 예외가 발생한다.") {
                    documentation(
                        identifier = "리뷰등록 실패",
                        tag = tag,
                        summary = summary,
                        description = description,
                    ) {
                        requestLine(HttpMethod.POST, "/reviews")

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                            header(HttpHeaders.CONTENT_TYPE, "Content-Type", "application/json")
                        }

                        requestBody {
                            field("orderNumber", "주문 번호", "ORD20250601123456789")
                            field("orderItemId", "주문 아이템 아이디", 1)
                            field("rating", "별점", 4)
                            field("content", "리뷰내용", "")
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", ReviewErrorCode.REVIEW_CONTENT_EMPTY.code)
                            field("error.message", "에러 메시지", ReviewErrorCode.REVIEW_CONTENT_EMPTY.message)
                        }
                    }
                }
            }
            describe("PUT /reviews/{reviewId} - 리뷰 수정") {
                val summary = "리뷰를 수정한다."
                val description = """
                RVW-001: 리뷰 내용을 입력해주세요.
                RVW-002: 별점은 1~5점사이로 선택해주세요.
                RVW-005: 존재하지 않는 리뷰입니다.
                RVW-006: 다른 사람의 리뷰를 수정 또는 삭제할 수 없습니다.
                """.trimMargin()

                it("리뷰를 수정할 수 있다.") {
                    documentation(
                        identifier = "리뷰수정 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                    ) {
                        requestLine(HttpMethod.PUT, "/reviews/{reviewId}") {
                            pathVariable("reviewId", "리뷰 아이디", 1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                            header(HttpHeaders.CONTENT_TYPE, "Content-Type", "application/json")
                        }

                        requestBody {
                            field("orderNumber", "주문 번호", "ORD20250601123456789")
                            field("orderItemId", "주문 아이템 아이디", 1)
                            field("rating", "별점", 4)
                            field("content", "리뷰내용", "맛있어요.")
                        }

                        responseBody {
                            field("data.reviewId", "수정된 리뷰 아이디", 1)
                            ignoredField("error")
                        }
                    }
                }
                it("빈 리뷰내용으로 리뷰를 수정하려고하면 예외가 발생한다.") {
                    documentation(
                        identifier = "리뷰수정 실패",
                        tag = tag,
                        summary = summary,
                        description = description,
                    ) {
                        requestLine(HttpMethod.PUT, "/reviews/{reviewId}") {
                            pathVariable("reviewId", "리뷰 아이디", 1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                            header(HttpHeaders.CONTENT_TYPE, "Content-Type", "application/json")
                        }

                        requestBody {
                            field("orderNumber", "주문 번호", "ORD20250601123456789")
                            field("orderItemId", "주문 아이템 아이디", 1)
                            field("rating", "별점", 4)
                            field("content", "리뷰내용", "")
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", ReviewErrorCode.REVIEW_CONTENT_EMPTY.code)
                            field("error.message", "에러 메시지", ReviewErrorCode.REVIEW_CONTENT_EMPTY.message)
                        }
                    }
                }
            }
            describe("DELETE /reviews/{reviewId} - 리뷰 삭제") {
                val summary = "리뷰를 삭제한다."
                val description = """
                RVW-005: 존재하지 않는 리뷰입니다.
                RVW-006: 다른 사람의 리뷰를 수정 또는 삭제할 수 없습니다.
                """.trimMargin()

                it("리뷰를 삭제할 수 있다.") {
                    documentation(
                        identifier = "리뷰삭제 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                    ) {
                        requestLine(HttpMethod.DELETE, "/reviews/{reviewId}") {
                            pathVariable("reviewId", "리뷰 아이디", 1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                            header(HttpHeaders.CONTENT_TYPE, "Content-Type", "application/json")
                        }

                        responseBody {
                            field("data.message", "삭제 응답", "OK")
                            ignoredField("error")
                        }
                    }
                }
                it("다른 사람의 리뷰를 삭제할 수 없다.") {
                    documentation(
                        identifier = "리뷰삭제 실패",
                        tag = tag,
                        summary = summary,
                        description = description,
                    ) {
                        requestLine(HttpMethod.DELETE, "/reviews/{reviewId}") {
                            pathVariable("reviewId", "리뷰 아이디", -1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                            header(HttpHeaders.CONTENT_TYPE, "Content-Type", "application/json")
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", ReviewErrorCode.HAS_NOT_AUTH.code)
                            field("error.message", "에러 메시지", ReviewErrorCode.HAS_NOT_AUTH.message)
                        }
                    }
                }
            }
        }
    }
