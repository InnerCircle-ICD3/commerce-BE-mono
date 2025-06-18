package com.fastcampus.commerce.order.interfaces

import com.fastcampus.commerce.common.response.EnumResponse
import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.order.application.order.OrderService
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.interfaces.request.OrderShippingInfoApiRequest
import com.fastcampus.commerce.order.interfaces.response.GetOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.GetOrderItemApiResponse
import com.fastcampus.commerce.order.interfaces.response.GetOrderShippingInfoApiResponse
import com.fastcampus.commerce.order.interfaces.response.OrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderItemApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderShippingInfoApiResponse
import com.fastcampus.commerce.order.interfaces.response.SearchOrderApiResponse
import com.fastcampus.commerce.restdoc.documentation
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
@WebMvcTest(OrderController::class)
@Import(TestConfig::class)
class OrderControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var orderService: OrderService

    val tag = "Order"

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("GET /orders/prepare - 주문서") {
            val summary = "주문서 생성"
            it("주문서를 생성할 수 있다.") {
                val response = PrepareOrderApiResponse(
                    cartItemIds = setOf(1L),
                    itemsSubtotal = 10000,
                    shippingFee = 3000,
                    finalTotalPrice = 13000,
                    items = listOf(
                        PrepareOrderItemApiResponse(
                            cartItemId = 1L,
                            productId = 1L,
                            name = "상품A",
                            thumbnail = "http://localhost:8080/api/v1/product/1/thumbnail",
                            unitPrice = 1000,
                            quantity = 10,
                            itemSubtotal = 10000,
                        ),
                    ),
                    shippingInfo = PrepareOrderShippingInfoApiResponse(
                        recipientName = "홍길동",
                        recipientPhone = "010-1234-1234",
                        zipCode = "12345",
                        addressId = 1L,
                        address1 = "서울특별시 관악구",
                        address2 = "서울대입구역 6번출구",
                    ),
                    paymentMethod = listOf(
                        EnumResponse("MOCK", "테스트"),
                    ),
                )
                every { orderService.prepareOrder(any(), any()) } returns response

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
                        field("cartItemIds", "장바구니 아이템 아이디", "1")
                    }

                    responseBody {
                        field("data.cartItemIds", "장바구니 아이템 아이디", listOf(1))
                        field("data.itemsSubtotal", "상품 금액 합계", 10000)
                        field("data.shippingFee", "배송비", 3000)
                        field("data.finalTotalPrice", "최종 결제 금액", 13000)
                        field("data.items[0].cartItemId", "장바구니 ID", 1)
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
                        field("data.shippingInfo.addressId", "배송지 아이디", 1)
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
                every { orderService.createOrder(any(), any()) } returns OrderApiResponse("ORD20250609123456789")

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
                val searchOrderApiResponse = SearchOrderApiResponse(
                    orderNumber = "ORD20250609123456789",
                    orderName = "스페셜 리버즈 외 3건",
                    mainProductThumbnail = "https://example.com/thumbnail.jpg",
                    orderStatus = OrderStatus.DELIVERED,
                    finalTotalPrice = 13000,
                    orderedAt = LocalDateTime.of(2025, 6, 8, 12, 34),
                    cancellable = true,
                    refundable = false,
                )
                val response = PageImpl(listOf(searchOrderApiResponse), PageRequest.of(1, 10), 1L)
                every { orderService.getOrders(any(), any()) } returns response
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
                        field("data.content[0].orderStatus", "주문 상태", OrderStatus.DELIVERED.name)
                        field("data.content[0].finalTotalPrice", "최종 결제 금액", 13000)
                        field("data.content[0].orderedAt", "주문 날짜", LocalDateTime.of(2025, 6, 8, 12, 34).toString())
                        field("data.content[0].cancellable", "취소 가능 여부", true)
                        field("data.content[0].refundable", "환불 가능 여부", false)
                        field("data.page", "현재 페이지 (기본값 1)", 2)
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
                val orderedAt = LocalDateTime.of(2025, 6, 8, 12, 34)
                val response = GetOrderApiResponse(
                    orderNumber = orderNumber,
                    orderName = "홍길동님의 주문",
                    orderStatus = "배송 준비중",
                    paymentNumber = "PAY1231414124",
                    paymentMethod = "토스페이",
                    itemsSubTotal = 10000,
                    shippingFee = 3000,
                    finalTotalPrice = 13000,
                    items = listOf(
                        GetOrderItemApiResponse(
                            orderItemId = 1L,
                            productSnapshotId = 1L,
                            name = "상품명",
                            thumbnail = "https://example.com/thumbnail.jpg",
                            unitPrice = 1000,
                            quantity = 10,
                            itemSubTotal = 10000,
                        ),
                    ),
                    shippingInfo = GetOrderShippingInfoApiResponse(
                        recipientName = "홍길동",
                        recipientPhone = "010-1234-1234",
                        zipCode = "08123",
                        address1 = "서울특별시 관악구",
                        address2 = "1000003동 123호",
                        deliveryMessage = "문앞에 놔주세요.",
                    ),
                    orderedAt = orderedAt,
                    paidAt = orderedAt,
                    cancellable = true,
                    cancelRequested = false,
                    cancelledAt = null,
                    refundable = false,
                    refundRequested = false,
                    refundRequestedAt = null,
                    refunded = false,
                    refundedAt = null,
                    reviewable = true,
                    reviewWritten = false,
                )
                every { orderService.getOrderDetail(orderNumber) } returns response

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

        describe("POST /orders/{orderNumber}/cancel - 주문 취소") {
            val summary = "주문을 취소합니다"

            it("주문_취소_성공") {
                val orderNumber = "ORD20250609123456789"
                every { orderService.cancelOrder(orderNumber) } returns Unit

                documentation(
                    identifier = "주문_취소_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.POST, "/orders/{orderNumber}/cancel") {
                        pathVariable("orderNumber", "주문 번호", orderNumber)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "인증 토큰", "Bearer sample-token")
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
