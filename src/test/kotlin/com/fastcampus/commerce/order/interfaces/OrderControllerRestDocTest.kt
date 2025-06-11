package com.fastcampus.commerce.order.interfaces

import com.fastcampus.commerce.config.TestSecurityConfig
import com.fastcampus.commerce.order.interfaces.request.OrderShippingInfoApiRequest
import com.fastcampus.commerce.restdoc.documentation
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDateTime

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(OrderController::class)
@Import(TestSecurityConfig::class)
class OrderControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    val tag = "Order"

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("POST /orders/prepare - 주문서") {
            val summary = "주문서 생성"
            it("주문서를 생성할 수 있다.") {
                documentation(
                    identifier = "주문서_생성_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/orders/prepare")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    queryParameters {
                        field("cartItemIds", "장바구니 아이템 아이디", "1,2,3")
                    }

                    responseBody {
                        field("data.cartItemIds", "장바구니 아이템 아이디", listOf(1))
                        field("data.itemsSubtotal", "상품 금액 합계", 10000)
                        field("data.shippingFee", "배송비", 3000)
                        field("data.finalTotalPrice", "최종 결제 금액", 13000)
                        field("data.items[0].productId", "상품 ID", 1)
                        field("data.items[0].name", "상품명", "상품A")
                        field(
                            "data.items[0].thumbnail",
                            "상품 썸네일 URL",
                            "http://localhost:8080/api/v1/product/1/thumbnail",
                        )
                        field("data.items[0].unitPrice", "상품 단가", 1000)
                        field("data.items[0].quantity", "수량", 10)
                        field("data.items[0].itemSubtotal", "상품 소계", 10000)
                        field("data.shippingInfo.recipientName", "수령인 이름", "홍길동")
                        field("data.shippingInfo.recipientPhone", "수령인 전화번호", "010-1234-1234")
                        field("data.shippingInfo.zipCode", "우편번호", "12345")
                        field("data.shippingInfo.address1", "주소", "서울특별시 관악구")
                        optionalField("data.shippingInfo.address2", "상세주소", "서울대입구역 6번출구")
                        field("data.paymentMethod[0].code", "결제 수단 코드", "MOCK")
                        field("data.paymentMethod[0].label", "결제 수단 이름", "테스트")
                        ignoredField("error")
                    }
                }
            }
        }

        describe("POST /orders - 주문 생성") {
            val summary = "주문을 생성합니다"

            it("주문_생성_성공") {
                documentation(
                    identifier = "주문_생성_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.POST, "/orders")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "인증 토큰", "Bearer sample-token")
                    }

                    requestBody {
                        field("cartItemIds", "장바구니 아이템 ID 목록", setOf(1L, 2L, 3L))
                        field(
                            "shippingInfo",
                            "배송지 정보",
                            OrderShippingInfoApiRequest(
                                recipientName = "홍길동",
                                recipientPhone = "010-1234-1234",
                                zipCode = "12345",
                                address1 = "서울특별시 관악구",
                                address2 = "서울대입구역 6번출구",
                                deliveryMessage = "문앞에 놔주세요",
                            ),
                        )
                        ignoredField("shippingInfo.recipientName")
                        ignoredField("shippingInfo.recipientPhone")
                        ignoredField("shippingInfo.zipCode")
                        ignoredField("shippingInfo.address1")
                        ignoredField("shippingInfo.address2")
                        ignoredField("shippingInfo.deliveryMessage")
                        field("paymentMethod", "결제 수단", "TOSS_PAY")
                    }

                    responseBody {
                        field("data.orderNumber", "주문 번호", "ORD20250609123456789")
                        ignoredField("error")
                    }
                }
            }
        }

        describe("GET /orders - 주문 목록 조회") {
            val summary = "주문 목록을 조회합니다"

            it("주문_목록_조회_성공") {
                documentation(
                    identifier = "주문_목록_조회_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/orders")

                    queryParameters {
                        field("page", "페이지 번호 (기본값: 1)", "1")
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "인증 토큰", "Bearer sample-token")
                    }

                    responseBody {
                        field("data.content[0].orderNumber", "주문 번호", "ORD20250609123456789")
                        field("data.content[0].orderName", "주문명", "스페셜 리버즈 외 3건")
                        field("data.content[0].mainProductThumbnail", "대표 상품 썸네일", "https://example.com/thumbnail.jpg")
                        field("data.content[0].orderStatus", "주문 상태", "배송 준비중")
                        field("data.content[0].finalTotalPrice", "최종 결제 금액", 13000)
                        field("data.content[0].orderedAt", "주문 날짜", LocalDateTime.of(2025, 6, 8, 12, 34).toString())
                        field("data.content[0].cancellable", "취소 가능 여부", true)
                        field("data.content[0].refundable", "환불 가능 여부", false)
                        field("data.page", "현재 페이지 (기본값 1)", 1)
                        field("data.size", "페이지 사이즈(기본값 10)", 10)
                        field("data.totalPages", "전체 페이지 수", 2)
                        field("data.totalElements", "총 수", 11)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("GET /orders/{orderNumber} - 주문 상세 조회") {
            val summary = "주문 상세 정보를 조회합니다"

            it("주문_상세_조회_성공") {
                val orderNumber = "ORD20250609123456789"

                documentation(
                    identifier = "주문_상세_조회_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/orders/{orderNumber}") {
                        pathVariable("orderNumber", "주문 번호", orderNumber)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "인증 토큰", "Bearer sample-token")
                    }

                    responseBody {
                        field("data.orderNumber", "주문 번호", orderNumber)
                        field("data.orderName", "주문명", "홍길동님의 주문")
                        field("data.orderStatus", "주문 상태", "배송 준비중")
                        field("data.paymentNumber", "결제 번호", "PAY1231414124")
                        field("data.paymentMethod", "결제 수단", "토스페이")
                        field("data.itemsSubTotal", "상품 금액 합계", 10000)
                        field("data.shippingFee", "배송비", 3000)
                        field("data.finalTotalPrice", "최종 결제 금액", 13000)
                        field("data.items[0].orderItemId", "주문 아이템 ID", 1)
                        field("data.items[0].productSnapshotId", "상품 스냅샷 ID", 1)
                        field("data.items[0].name", "상품명", "상품명")
                        field("data.items[0].thumbnail", "상품 썸네일 URL", "https://example.com/thumbnail.jpg")
                        field("data.items[0].unitPrice", "상품 단가", 1000)
                        field("data.items[0].quantity", "수량", 10)
                        field("data.items[0].itemSubTotal", "상품 소계", 10000)
                        field("data.shippingInfo.recipientName", "수령인 이름", "홍길동")
                        field("data.shippingInfo.recipientPhone", "수령인 전화번호", "010-1234-1234")
                        field("data.shippingInfo.zipCode", "우편번호", "08123")
                        field("data.shippingInfo.address1", "주소", "서울특별시 관악구")
                        field("data.shippingInfo.address2", "상세주소", "1000003동 123호")
                        field("data.shippingInfo.deliveryMessage", "배송 메시지", "문앞에 놔주세요.")
                        field("data.orderedAt", "주문 일시", "2025-06-08T12:34")
                        field("data.paidAt", "결제 일시", "2025-06-08T12:34")
                        field("data.cancellable", "취소 가능 여부", true)
                        field("data.cancelRequested", "취소 요청 여부", false)
                        optionalField("data.cancelledAt", "취소 일시", null)
                        field("data.refundable", "환불 가능 여부", false)
                        field("data.refundRequested", "환불 요청 여부", false)
                        optionalField("data.refundRequestedAt", "환불 요청 일시", null)
                        field("data.refunded", "환불 완료 여부", false)
                        optionalField("data.refundedAt", "환불 완료 일시", null)
                        field("data.reviewable", "리뷰 작성 가능 여부", true)
                        field("data.reviewWritten", "리뷰 작성 여부", false)
                        ignoredField("error")
                    }
                }
            }
        }
    }
}
