package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.restdoc.documentation
import com.fastcampus.commerce.review.application.ReviewCommandService
import com.fastcampus.commerce.review.interfaces.request.RegisterReviewApiRequest
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(ReviewController::class)
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
    }
}
