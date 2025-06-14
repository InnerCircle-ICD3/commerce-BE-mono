package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.restdoc.documentation
import com.fastcampus.commerce.review.application.ProductReviewService
import com.fastcampus.commerce.review.application.response.AdminReplyResponse
import com.fastcampus.commerce.review.application.response.ProductReviewRatingResponse
import com.fastcampus.commerce.review.application.response.ProductReviewResponse
import com.fastcampus.commerce.review.application.response.RatingDistributionResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.restassured.config.JsonConfig
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.path.json.config.JsonPathConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDateTime

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(ProductReviewController::class)
@Import(TestConfig::class)
class ProductReviewControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var productReviewService: ProductReviewService

    val tag = "Review"

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
            RestAssuredMockMvc.config = RestAssuredMockMvc
                .config()
                .jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE))
        }

        describe("GET /product/{productId}/reviews - 상품 리뷰 목록 조회") {
            val summary = "상품에 등록된 리뷰 목록 조회"
            it("상품에 등록된 리뷰 목록을 조회할 수 있다.") {
                val productId = 1L

                val now = LocalDateTime.of(2025, 6, 1, 12, 0)
                val productReviews = listOf(
                    ProductReviewResponse(
                        reviewId = 1L,
                        rating = 5,
                        content = "아주 만족해요",
                        createdAt = now,
                        adminReply = AdminReplyResponse(
                            content = "감사합니다",
                            createdAt = now,
                        ),
                    ),
                )
                val response = PageImpl(productReviews, PageRequest.of(1, 10), productReviews.size.toLong())
                every {
                    productReviewService.getProductReviews(productId, any())
                } returns response

                documentation(
                    identifier = "상품_리뷰목록_조회_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/products/{productId}/reviews") {
                        pathVariable("productId", "상품 아이디", productId)
                    }

                    queryParameters {
                        optionalField("page", "페이지(기본 값: 1)", 1)
                    }

                    responseBody {
                        field("data.content[0].reviewId", "리뷰 아이디", productReviews[0].reviewId.toInt())
                        field("data.content[0].rating", "별점", productReviews[0].rating)
                        field("data.content[0].content", "리뷰 내용", productReviews[0].content)
                        field("data.content[0].createdAt", "리뷰 작성일", productReviews[0].createdAt.toString())
                        optionalField(
                            "data.content[0].adminReply.content",
                            "관리자 답글 내용",
                            productReviews[0].adminReply?.content,
                        )
                        optionalField(
                            "data.content[0].adminReply.createdAt",
                            "관리자 답글 작성일",
                            productReviews[0].adminReply?.createdAt.toString(),
                        )
                        field("data.page", "현재 페이지 (기본값 1)", response.number)
                        field("data.size", "페이지 사이즈(기본값 10)", response.size)
                        field("data.totalPages", "전체 페이지 수", response.totalPages)
                        field("data.totalElements", "총 수", response.totalElements.toInt())
                        ignoredField("error")
                    }
                }
            }
        }

        describe("GET /product/{productId}/reviews/rating - 상품 리뷰 별점 통계") {
            val summary = "상품에 등록된 리뷰 별점 통계 조회"

            it("상품에 등록된 리뷰의 별점 통계를 조회할 수 있다.") {
                val response = ProductReviewRatingResponse(
                    averageRating = 3.5,
                    totalCount = 10,
                    ratingDistribution = RatingDistributionResponse(
                        oneStarCount = 2,
                        twoStarsCount = 3,
                        threeStarsCount = 5,
                        fourStarsCount = 1,
                        fiveStarsCount = 8,
                    ),
                )
                every { productReviewService.getProductReviewRating(any()) } returns response

                documentation(
                    identifier = "상품_리뷰별점통계_조회_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/products/{productId}/reviews/rating") {
                        pathVariable("productId", "상품 아이디", 1)
                    }

                    responseBody {
                        field("data.averageRating", "평점", response.averageRating)
                        field("data.totalCount", "리뷰 개수", response.totalCount.toInt())
                        field("data.ratingDistribution.oneStarCount", "별점 1점 개수", response.ratingDistribution.oneStarCount)
                        field("data.ratingDistribution.twoStarsCount", "별점 2점 개수", response.ratingDistribution.twoStarsCount)
                        field("data.ratingDistribution.threeStarsCount", "별점 3점 개수", response.ratingDistribution.threeStarsCount)
                        field("data.ratingDistribution.fourStarsCount", "별점 4점 개수", response.ratingDistribution.fourStarsCount)
                        field("data.ratingDistribution.fiveStarsCount", "별점 5점 개수", response.ratingDistribution.fiveStarsCount)
                        ignoredField("error")
                    }
                }
            }
        }
    }
}
