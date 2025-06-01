package com.fastcampus.commerce.admin.review.interfaces

import com.fastcampus.commerce.admin.review.error.AdminReviewErrorCode
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
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
class AdminReviewControllerRestDocTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) : DescribeSpec() {
        override fun extensions() = listOf(SpringExtension)

        init {
            beforeSpec {
                RestAssuredMockMvc.mockMvc(mockMvc)
            }

            val tag = "Admin-Review"
            val privateResource = true

            describe("GET /admin/reviews") {
                val summary = "관리자가 리뷰를 검색한다."

                it("리뷰를 검색할 수 있다.") {
                    documentation(
                        identifier = "관리자_리뷰검색_성공",
                        tag = tag,
                        summary = summary,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.GET, "/admin/reviews")

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        queryParameters {
                            optionalField("productId", "상품아이디", 1)
                            optionalField("productName", "상품명", "스타벅스")
                            optionalField("content", "리뷰 내용(부분 검색)", "좋아")
                            optionalField("from", "리뷰 작성일(from)", LocalDate.now().toString())
                            optionalField("to", "리뷰 작성일(to)", LocalDate.now().plusDays(1).toString())
                        }

                        responseBody {
                            field("data.content[0].reviewId", "리뷰 아이디", 1)
                            field("data.content[0].rating", "별점", 4)
                            field("data.content[0].content", "리뷰 내용", "좋아요")
                            field("data.content[0].createdAt", "리뷰 작성일", LocalDateTime.now().toString())
                            optionalField("data.content[0].adminReply.content", "관리자의 리뷰 답글 내용", "감사합니다")
                            optionalField(
                                "data.content[0].adminReply.createdAt",
                                "관리자의 리뷰 답글 작성일",
                                LocalDateTime.now().toString(),
                            )
                            field("data.content[0].productId", "리뷰 상품 아이디", 1)
                            field("data.content[0].productName", "리뷰 상품명", "스타벅스 캡슐")
                            field("data.content[0].productThumbnail", "리뷰 상품 썸네일", "https://example.com/thumbnail.jpg")
                            field("data.page", "현재 페이지 (기본값 1)", 1)
                            field("data.size", "페이지 사이즈(기본값 10)", 10)
                            field("data.totalPages", "전체 페이지 수", 10)
                            field("data.totalElements", "총 수", 99)
                            ignoredField("error")
                        }
                    }
                }
            }
            describe("POST /admin/reviews - 리뷰 답글 등록") {

                val summary = "관리자가 리뷰 답글을 등록한다."
                val description = """
                    ARV-001: 존재하지 않는 리뷰입니다.
                    ARV-002: 리뷰 답글이 비어있습니다.
                """.trimMargin()

                it("리뷰 답글을 등록할 수 있다.") {
                    documentation(
                        identifier = "관리자 리뷰답글등록 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.POST, "/admin/reviews/{reviewId}/reply") {
                            pathVariable("reviewId", "리뷰 아이디", 1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        requestBody {
                            field("content", "답글 내용", "감사합니다")
                        }

                        responseBody {
                            field("data.replyId", "등록된 리뷰 답글 아이디", 1)
                            ignoredField("error")
                        }
                    }
                }
                it("리뷰 답글 등록 요청시 내용이 비어있으면 예외가 발생한다.") {
                    documentation(
                        identifier = "관리자 리뷰답글등록 실패",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.POST, "/admin/reviews/{reviewId}/reply") {
                            pathVariable("reviewId", "리뷰 아이디", 1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        requestBody {
                            field("content", "답글 내용", "")
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", AdminReviewErrorCode.REPLY_CONTENT_EMPTY.code)
                            field("error.message", "에러 메시지", AdminReviewErrorCode.REPLY_CONTENT_EMPTY.message)
                        }
                    }
                }
            }
            describe("PUT /reviews/{reviewId}/reply/{replyId} - 리뷰 답글 수정") {
                val summary = "관리자가 리뷰답글을 수정한다."
                val description = """
                    ARV-001: 존재하지 않는 리뷰입니다.
                    ARV-002: 리뷰 답글이 비어있습니다.
                    ARV-003: 존재하지 않는 리뷰 답글입니다.
                """.trimMargin()

                it("리뷰답글 정보를 수정할 수 있다.") {
                    documentation(
                        identifier = "관리자 리뷰답글수정 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.PUT, "/admin/reviews/{reviewId}/reply/{replyId}") {
                            pathVariable("reviewId", "리뷰 아이디", 1)
                            pathVariable("replyId", "리뷰답글 아이디", 1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        requestBody {
                            field("content", "답글 내용", "감사합니다.")
                        }

                        responseBody {
                            field("data.replyId", "수정된 리뷰답글 아이디", 1)
                            ignoredField("error")
                        }
                    }
                }
                it("존재하지 않은 리뷰답글을 수정하려고 하면 예외가 발생한다.") {
                    documentation(
                        identifier = "관리자 리뷰답글수정 실패",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.PUT, "/admin/reviews/{reviewId}/reply/{replyId}") {
                            pathVariable("reviewId", "리뷰 아이디", 1)
                            pathVariable("replyId", "리뷰답글 아이디", -1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        requestBody {
                            field("content", "답글 내용", "감사합니다.")
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", AdminReviewErrorCode.REPLY_NOT_EXISTS.code)
                            field("error.message", "에러 메시지", AdminReviewErrorCode.REPLY_NOT_EXISTS.message)
                        }
                    }
                }
            }
            describe("DELETE /reviews/{reviewId}/reply/{replyId} - 리뷰 답글 삭제") {
                val summary = "관리자가 리뷰답글을 삭제한다."
                val description = """
                    APD-008: 존재하지 않는 리뷰답글입니다.
                """.trimMargin()

                it("리뷰답글을 삭제할 수 있다.") {
                    documentation(
                        identifier = "관리자 리뷰답글삭제 성공",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.DELETE, "/admin/reviews/{reviewId}/reply/{replyId}") {
                            pathVariable("reviewId", "리뷰 아이디", 1)
                            pathVariable("replyId", "리뷰답글 아이디", 1)
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
                it("존재하지 않은 리뷰답글을 삭제하려고 하면 예외가 발생한다.") {
                    documentation(
                        identifier = "관리자 리뷰답글삭제  실패",
                        tag = tag,
                        summary = summary,
                        description = description,
                        privateResource = privateResource,
                    ) {
                        requestLine(HttpMethod.DELETE, "/admin/reviews/{reviewId}/reply/{replyId}") {
                            pathVariable("reviewId", "리뷰 아이디", 1)
                            pathVariable("replyId", "리뷰답글 아이디", -1)
                        }

                        requestHeaders {
                            header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-auth-key")
                        }

                        responseBody {
                            ignoredField("data")
                            field("error.code", "에러 코드", AdminReviewErrorCode.REPLY_NOT_EXISTS.code)
                            field("error.message", "에러 메시지", AdminReviewErrorCode.REPLY_NOT_EXISTS.message)
                        }
                    }
                }
            }
        }
    }
