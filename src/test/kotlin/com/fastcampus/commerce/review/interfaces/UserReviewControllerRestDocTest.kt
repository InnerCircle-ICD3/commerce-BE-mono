package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.restdoc.documentation
import com.fastcampus.commerce.review.application.UserReviewService
import com.fastcampus.commerce.review.application.response.UserReviewAdminReplyResponse
import com.fastcampus.commerce.review.application.response.UserReviewProductResponse
import com.fastcampus.commerce.review.application.response.UserReviewResponse
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
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDateTime

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(UserReviewController::class)
@Import(TestConfig::class)
class UserReviewControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var userReviewService: UserReviewService

    val tag = "Review"

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("GET /reviews:byAuthor - 사용자 리뷰 목록 조회") {
            val summary = "사용자가 등록한 리뷰 목록 조회"
            it("사용자가 등록한 리뷰 목록을 조회할 수 있다.") {
                val userId = 1L

                val now = LocalDateTime.of(2025, 6, 1, 12, 0)
                val userReviews = listOf(
                    UserReviewResponse(
                        reviewId = 1L,
                        rating = 5,
                        content = "아주 만족해요",
                        adminReply = UserReviewAdminReplyResponse(
                            content = "감사합니다",
                            createdAt = now,
                        ),
                        product = UserReviewProductResponse(
                            productId = 1L,
                            productName = "상품",
                            productThumbnail = "https://example.com/product/1.jpg",
                        ),
                        createdAt = now,
                    ),
                )
                val response = PageImpl(userReviews, PageRequest.of(1, 10), userReviews.size.toLong())
                every {
                    userReviewService.getReviewsByAuthor(any(), any())
                } returns response

                documentation(
                    identifier = "사용자_리뷰목록_조회_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/reviews:byAuthor")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    queryParameters {
                        optionalField("monthRange", "작성 월(3,6,9,12개월)", 3)
                        optionalField("page", "페이지(기본 값: 1)", 1)
                        optionalField("sort", "정렬(기본값: createdAt / e.g. sort=createdAt, sort=-createdAt)", "createdAt")
                    }

                    responseBody {
                        field("data.content[0].reviewId", "리뷰 아이디", userReviews[0].reviewId.toInt())
                        field("data.content[0].rating", "별점", userReviews[0].rating)
                        field("data.content[0].content", "리뷰 내용", userReviews[0].content)
                        optionalField(
                            "data.content[0].adminReply.content",
                            "관리자 답글 내용",
                            userReviews[0].adminReply?.content,
                        )
                        optionalField(
                            "data.content[0].adminReply.createdAt",
                            "관리자 답글 작성일",
                            userReviews[0].adminReply?.createdAt.toString(),
                        )
                        field("data.content[0].product.productId", "상품 아이디", userReviews[0].product.productId.toInt())
                        field("data.content[0].product.productName", "상품명", userReviews[0].product.productName)
                        field("data.content[0].product.productThumbnail", "상품 썸네일", userReviews[0].product.productThumbnail)
                        field("data.content[0].createdAt", "리뷰 작성일", userReviews[0].createdAt.toString())
                        field("data.page", "현재 페이지 (기본값 1)", response.number + 1)
                        field("data.size", "페이지 사이즈(기본값 10)", response.size)
                        field("data.totalPages", "전체 페이지 수", response.totalPages)
                        field("data.totalElements", "총 수", response.totalElements.toInt())
                        ignoredField("error")
                    }
                }
            }
        }
    }
}
