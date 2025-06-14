package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.restdoc.documentation
import com.fastcampus.commerce.review.application.ReviewCommandService
import com.fastcampus.commerce.review.interfaces.request.RegisterReviewApiRequest
import com.fastcampus.commerce.review.interfaces.request.UpdateReviewApiRequest
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
@WebMvcTest(ReviewController::class)
@Import(TestConfig::class)
class ReviewControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var reviewCommandService: ReviewCommandService

    val tag = "Review"

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("POST /reviews - 리뷰 등록") {
            val summary = "배송완료된 상품에 리뷰를 등록할 수 있다."
            val description = """
                RVW-001: 리뷰 내용을 입력해주세요.
                RVW-002: 별점은 1~5점 사이로 선택해주세요.
                RVW-003: 배송완료된 주문건에 대해서만 리뷰를 작성할 수 있습니다.
                RVW-004: 배송완료 후 30일 이내의 주문건에 대해서만 리뷰를 작성 및 수정할 수 있습니다.
                RVW-005: 리뷰는 주문 당 한번만 작성가능합니다.
            """.trimMargin()
            it("배송완료된 상품에 리뷰를 등록할 수 있다.") {
                val userId = 1L
                val request = RegisterReviewApiRequest(
                    orderNumber = "ORDER1234",
                    orderItemId = 10L,
                    rating = 3,
                    content = "좋습니다.",
                )

                every { reviewCommandService.registerReview(userId, request.toServiceRequest()) } returns 10L

                documentation(
                    identifier = "리뷰_등록_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                ) {
                    requestLine(HttpMethod.POST, "/reviews")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("orderNumber", "주문번호", request.orderNumber)
                        field("orderItemId", "주문 아이템 아이디", request.orderItemId)
                        field("rating", "별점", request.rating)
                        field("content", "리뷰 내용", request.content)
                    }

                    responseBody {
                        field("data.reviewId", "생성된 리뷰 아이디", 10)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("PUT /reviews/{reviewId} - 리뷰 수정") {
            val summary = "리뷰를 수정할 수 있다."
            val description = """
                RVW-001: 리뷰 내용을 입력해주세요.
                RVW-002: 별점은 1~5점 사이로 선택해주세요.
                RVW-006: 리뷰를 찾을 수 없습니다.
                RVW-007: 다른 사람의 리뷰를 수정할 수 없습니다
            """.trimMargin()

            it("리뷰를 수정할 수 있다.") {
                val reviewId = 10L
                val userId = 1L
                val request = UpdateReviewApiRequest(
                    rating = 3,
                    content = "좋습니다.",
                )

                every { reviewCommandService.updateReview(userId, reviewId, request.toServiceRequest()) } returns reviewId

                documentation(
                    identifier = "리뷰_수정_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                ) {
                    requestLine(HttpMethod.PUT, "/reviews/{reviewId}") {
                        pathVariable("reviewId", "리뷰 아이디", reviewId)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("rating", "별점", request.rating)
                        field("content", "리뷰 내용", request.content)
                    }

                    responseBody {
                        field("data.reviewId", "수정된 리뷰 아이디", reviewId.toInt())
                        ignoredField("error")
                    }
                }
            }
        }

        describe("DELETE /reviews/{reviewId} - 리뷰 삭제") {
            val summary = "리뷰를 삭제할 수 있다."
            val description = """
                RVW-006: 리뷰를 찾을 수 없습니다.
                RVW-008: 다른 사람의 리뷰를 삭제할 수 없습니다
            """.trimMargin()
            it("리뷰를 삭제할 수 있다.") {
                val userId = 1L
                val reviewId = 10L

                every { reviewCommandService.deleteReview(userId, reviewId) } just Runs

                documentation(
                    identifier = "리뷰_삭제_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                ) {
                    requestLine(HttpMethod.DELETE, "/reviews/{reviewId}") {
                        pathVariable("reviewId", "리뷰 아이디", reviewId)
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
