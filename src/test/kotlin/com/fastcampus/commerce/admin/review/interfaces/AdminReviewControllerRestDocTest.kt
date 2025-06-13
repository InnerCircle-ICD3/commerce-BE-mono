package com.fastcampus.commerce.admin.review.interfaces

import com.fastcampus.commerce.admin.review.application.AdminReviewService
import com.fastcampus.commerce.admin.review.application.response.SearchReviewAdminAuthorResponse
import com.fastcampus.commerce.admin.review.application.response.SearchReviewAdminProductResponse
import com.fastcampus.commerce.admin.review.application.response.SearchReviewAdminReplyResponse
import com.fastcampus.commerce.admin.review.application.response.SearchReviewAdminResponse
import com.fastcampus.commerce.config.TestConfig
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
import java.time.LocalDateTime

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(AdminReviewController::class)
@Import(TestConfig::class)
class AdminReviewControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminReviewService: AdminReviewService

    val tag = "Admin-Review"
    val privateResource = true

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("GET /admin/reviews - 리뷰 조회") {
            val summary = "관리자의 리뷰 검색"
            it("리뷰를 검색할 수 있다.") {
                val now = LocalDateTime.of(2025, 6, 12, 12, 0, 0)
                val reviews = listOf(
                    SearchReviewAdminResponse(
                        reviewId = 10L,
                        rating = 5,
                        content = "좋아요.",
                        adminReply = SearchReviewAdminReplyResponse("감사합니다.", now),
                        user = SearchReviewAdminAuthorResponse(1L, "커피좋아"),
                        product = SearchReviewAdminProductResponse(1L, "스타벅스 캡슐커피"),
                        createdAt = now,
                    ),
                )
                val response = PageImpl(reviews, PageRequest.of(1, 10), reviews.size.toLong())
                every { adminReviewService.search(any(), any()) } returns response

                documentation(
                    identifier = "관리자_리뷰목록_조회_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.GET, "/admin/reviews")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    queryParameters {
                        optionalField("productId", "상품 아이디", 1)
                        optionalField("productName", "상품명", "스타벅스")
                        optionalField("rating", "별점(1~5)", 5)
                        optionalField("content", "리뷰 내용", "좋아요")
                        optionalField("period", "작성일 기간(3, 6, 9, 12월)", 3)
                        optionalField("page", "페이지(기본 값: 1)", 1)
                    }

                    responseBody {
                        field("data.content[0].reviewId", "리뷰 아이디", reviews[0].reviewId.toInt())
                        field("data.content[0].rating", "별점", reviews[0].rating)
                        field("data.content[0].content", "리뷰 내용", reviews[0].content)
                        optionalField("data.content[0].adminReply.content", "관리자 답글 내용", reviews[0].adminReply?.content)
                        optionalField("data.content[0].adminReply.createdAt", "관리자 답글 작성일", reviews[0].adminReply?.createdAt.toString())
                        field("data.content[0].user.userId", "리뷰 작성자 아이디", reviews[0].user.userId.toInt())
                        field("data.content[0].user.nickname", "리뷰 작성자 닉네임", reviews[0].user.nickname)
                        field("data.content[0].product.productId", "리뷰 대상 상품 아이디", reviews[0].product.productId.toInt())
                        field("data.content[0].product.productName", "리뷰 대상 상품명", reviews[0].product.productName)
                        field("data.content[0].createdAt", "리뷰 작성일", reviews[0].createdAt.toString())
                        field("data.page", "현재 페이지 (기본값 1)", response.number)
                        field("data.size", "페이지 사이즈(기본값 10)", response.size)
                        field("data.totalPages", "전체 페이지 수", response.totalPages)
                        field("data.totalElements", "총 수", response.totalElements.toInt())
                        ignoredField("error")
                    }
                }
            }
        }

        describe("POST /admin/reviews/{reviewId}/reply - 리뷰 답글등록") {
            val summary = "관리자의 리뷰 답글 등록"
            it("리뷰 답글을 등록할 수 있다.") {
                val replyId = 1L
                every { adminReviewService.registerReply(any(), any(), any()) } returns replyId

                documentation(
                    identifier = "관리자_리뷰답글_등록_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.POST, "/admin/reviews/{reviewId}/reply") {
                        pathVariable("reviewId", "리뷰 아이디", 1)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("content", "답글 내용", "감사합니다.")
                    }

                    responseBody {
                        field("data.replyId", "리뷰 답글 아이디", 1)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("PUT /admin/reviews/reply/{replyId} - 리뷰 답글수정") {
            val summary = "관리자의 리뷰 답글 수정"
            it("리뷰 답글을 수정할 수 있다.") {
                every { adminReviewService.updateReply(any(), any(), any()) } just Runs

                documentation(
                    identifier = "관리자_리뷰답글_수정_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.PUT, "/admin/reviews/reply/{replyId}") {
                        pathVariable("replyId", "리뷰 답글 아이디", 1)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("content", "답글 내용", "감사합니다.")
                    }

                    responseBody {
                        field("data.replyId", "리뷰 답글 아이디", 1)
                        ignoredField("error")
                    }
                }
            }
        }
    }
}
