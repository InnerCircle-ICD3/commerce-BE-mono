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
import org.springframework.http.HttpMethod
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
class ReviewByProductControllerRestDocTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) : DescribeSpec() {
        override fun extensions() = listOf(SpringExtension)

        init {
            beforeSpec {
                RestAssuredMockMvc.mockMvc(mockMvc)
            }
            val tag = "Review"

            describe("GET /products/{productId}/reviews - 상품 리뷰 목록") {

                it("상품 리뷰 목록을 조회할 수 있다.") {
                    documentation(
                        identifier = "상품 리뷰목록 조회 성공",
                        tag = tag,
                        summary = "상품에 속한 리뷰 목록을 조회한다.",
                    ) {
                        requestLine(HttpMethod.GET, "/products/{productId}/reviews") {
                            pathVariable("productId", "상품 아이디", 1)
                        }

                        queryParameters {
                            optionalField("page", "요청 페이지", 1)
                        }

                        responseBody {
                            field("data.content[0].reviewId", "리뷰 아이디", 1)
                            field("data.content[0].rating", "별점", 5)
                            field("data.content[0].content", "리뷰 내용", "배송이 빨라요.")
                            field("data.content[0].createdAt", "리뷰 작성일", "2025-06-01T11:45:12")
                            optionalField("data.content[0].adminReply.content", "관리자의 리뷰 답글 내용", "감사합니다.")
                            optionalField("data.content[0].adminReply.createdAt", "관리자의 리뷰 답글 작성일", "2025-06-01T11:45:12")
                            field("data.content[0].productId", "리뷰 상품아이디", 1)
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
            describe("GET /products/{productId}/reviews/rating - 상품 리뷰 통계") {

                it("상품 리뷰 통계를 조회할 수 있다.") {
                    documentation(
                        identifier = "상품 리뷰통계 조회 성공",
                        tag = tag,
                        summary = "상품에 속한 리뷰 통계를 조회한다.",
                    ) {
                        requestLine(HttpMethod.GET, "/products/{productId}/reviews/rating") {
                            pathVariable("productId", "상품 아이디", 1)
                        }

                        responseBody {
                            field("data.averageRating", "리뷰 평균 별점", 3.5)
                            field("data.ratingDistribution.oneStarCount", "리뷰 별점 1점 분포", 1)
                            field("data.ratingDistribution.twoStarsCount", "리뷰 별점 2점 분포", 1)
                            field("data.ratingDistribution.threeStarsCount", "리뷰 별점 3점 분포", 1)
                            field("data.ratingDistribution.fourStarsCount", "리뷰 별점 4점 분포", 1)
                            field("data.ratingDistribution.fiveStarsCount", "리뷰 별점 5점 분포", 1)
                            ignoredField("error")
                        }
                    }
                }
            }
        }
    }
