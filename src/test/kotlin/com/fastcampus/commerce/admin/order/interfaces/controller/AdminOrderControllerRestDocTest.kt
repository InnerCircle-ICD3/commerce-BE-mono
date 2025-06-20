package com.fastcampus.commerce.admin.order.interfaces.controller

import com.fastcampus.commerce.admin.order.application.AdminOrderService
import com.fastcampus.commerce.admin.order.interfaces.AdminOrderController
import com.fastcampus.commerce.admin.order.interfaces.response.AdminOrderDetailItemResponse
import com.fastcampus.commerce.admin.order.interfaces.response.AdminOrderDetailResponse
import com.fastcampus.commerce.admin.order.interfaces.response.AdminOrderDetailShippingInfoResponse
import com.fastcampus.commerce.admin.order.interfaces.response.AdminOrderListResponse
import com.fastcampus.commerce.config.TestConfig
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
@WebMvcTest(AdminOrderController::class)
@Import(TestConfig::class)
class AdminOrderControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminOrderService: AdminOrderService

    val tag = "Admin-Order"
    val privateResource = true

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("GET /admin/orders - 주문 검색") {
            val summary = "관리자 주문검색"
            it("주문을 검색할 수 있다.") {
                val orderDate = LocalDateTime.of(2025, 6, 10, 12, 0)
                val searchResponse = listOf(
                    AdminOrderListResponse(
                        orderId = 1L,
                        orderNumber = "ORD123",
                        orderName = "ㅁㄴㅇㄹ 외 1건",
                        orderStatus = "결제완료",
                        finalTotalPrice = 1000,
                        orderedAt = orderDate,
                        trackingNumber = "4231908234098",
                        customerId = 1L,
                        customerName = "홍길동",
                    ),
                )
                val response = PageImpl(
                    searchResponse,
                    PageRequest.of(0, 10),
                    1L,
                )
                every { adminOrderService.getOrders(any(), any()) } returns response

                documentation(
                    identifier = "관리자_주문목록_조회_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.GET, "/admin/orders")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    queryParameters {
                        optionalField("orderNumber", "주문번호(eq 검색)", "ORD1234")
                        optionalField("nickname", "주문자 닉네임(eq 검색)", "홍길동")
                        optionalField("productName", "상품명(like 검색)", "콜드")
                        optionalField("status", "주문상태 코드", "PAID")
                        optionalField("dateFrom", "주문날짜 From(yyyy.MM.dd)", "2025.01.01")
                        optionalField("dateTo", "주문날짜 To(yyyy.MM.dd)", "2025.03.01")
                        optionalField("page", "페이지 번호 (1부터 시작, 기본값: 1)", 1)
                    }

                    responseBody {
                        field("data.content[0].orderId", "주문 ID", searchResponse[0].orderId.toInt())
                        field("data.content[0].orderNumber", "주문번호", searchResponse[0].orderNumber)
                        field("data.content[0].orderName", "주문명", searchResponse[0].orderName)
                        field("data.content[0].orderStatus", "주문 상태 라벨", searchResponse[0].orderStatus)
                        field(
                            "data.content[0].finalTotalPrice",
                            "최종결제 금액(상품금액 + 배송비)",
                            searchResponse[0].finalTotalPrice,
                        )
                        field("data.content[0].orderedAt", "주문날짜", searchResponse[0].orderedAt.toString())
                        optionalField(
                            "data.content[0].trackingNumber",
                            "송장번호",
                            searchResponse[0].trackingNumber,
                        )
                        field(
                            "data.content[0].customerId",
                            "주문자 아이디",
                            searchResponse[0].customerId.toInt(),
                        )
                        field("data.content[0].customerName", "주문자 닉네임", searchResponse[0].customerName)
                        field("data.page", "현재 페이지 번호", response.number + 1)
                        field("data.size", "페이지 크기", response.size)
                        field("data.totalPages", "전체 페이지 수", response.totalPages)
                        field("data.totalElements", "총 상품 수", response.totalElements.toInt())
                        ignoredField("error")
                    }
                }
            }
        }

        describe("GET /admin/orders/{orderId} - 주문 상세 조회") {
            var summary = "주문 상세 조회"
            it("주문 상세조회를 할 수 있다.") {
                val orderDate = LocalDateTime.of(2025, 6, 10, 12, 0)
                val response = AdminOrderDetailResponse(
                    orderId = 1L,
                    orderNumber = "ORD20250620258727916",
                    orderStatus = "배송중",
                    trackingNumber = "234980234",
                    paymentNumber = "PAY20250620347436652",
                    paymentMethod = "MOCK",
                    paymentStatus = "결제 완료",
                    itemsSubTotal = 1834200,
                    shippingFee = 0,
                    finalTotalPrice = 1834200,
                    items = listOf(
                        AdminOrderDetailItemResponse(
                            orderItemId = 20391,
                            productId = 21,
                            name = "돌체구스토 라떼 마키아토 16캡슐",
                            thumbnail = "https://801base.s3.ap-northeast-2.amazonaws.com/products/thumbnail-f9864097.jpg",
                            unitPrice = 10900,
                            quantity = 5,
                            itemSubTotal = 54500,
                        ),
                    ),
                    shippingInfo = AdminOrderDetailShippingInfoResponse(
                        recipientName = "홍길동",
                        recipientPhone = "010-1234-1234",
                        zipCode = "08123",
                        address1 = "서울특별시 관악구",
                        address2 = "123동 123호",
                        deliveryMessage = "문앞에 두고가주세요",
                    ),
                    orderedAt = orderDate,
                    paidAt = orderDate,
                    cancellable = true,
                    cancelRequested = false,
                    cancelledAt = orderDate,
                    refundable = false,
                    refundRequested = false,
                    refundRequestedAt = orderDate,
                    refunded = true,
                    refundedAt = orderDate,
                )
                every { adminOrderService.getOrderDetail(any()) } returns response
                documentation(
                    identifier = "관리자_주문상세_조회_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.GET, "/admin/orders/{orderId}") {
                        pathVariable("orderId", "주문 아이디", 1)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    responseBody {
                        field("data.orderId", "주문 아이디", response.orderId.toInt())
                        field("data.orderNumber", "주문번호", response.orderNumber)
                        field("data.orderStatus", "주문상태 라벨", response.orderStatus)
                        optionalField("data.trackingNumber", "송장번호", response.trackingNumber)
                        field("data.paymentNumber", "결제번호", response.paymentNumber)
                        field("data.paymentMethod", "결제방법", response.paymentMethod)
                        field("data.paymentStatus", "결제상태", response.paymentStatus)
                        field("data.itemsSubTotal", "주문항목 총액", response.itemsSubTotal)
                        field("data.shippingFee", "배송비", response.shippingFee)
                        field("data.finalTotalPrice", "최종 결제금액", response.finalTotalPrice)
                        field("data.items[0].orderItemId", "주문항목 아이디", response.items[0].orderItemId.toInt())
                        field("data.items[0].productId", "주문항목 상품 아이디", response.items[0].productId.toInt())
                        field("data.items[0].name", "주문항목 상품명", response.items[0].name)
                        field("data.items[0].thumbnail", "주문항목 상품썸네일", response.items[0].thumbnail)
                        field("data.items[0].unitPrice", "주문항목 상품가격", response.items[0].unitPrice)
                        field("data.items[0].quantity", "주문항목 주문수량", response.items[0].quantity)
                        field("data.items[0].itemSubTotal", "주문항목 총액(항목 가격 * 주문수량)", response.items[0].itemSubTotal)
                        field("data.shippingInfo.recipientName", "받는사람 이름", response.shippingInfo.recipientName)
                        field("data.shippingInfo.recipientPhone", "받는사람 연락처", response.shippingInfo.recipientPhone)
                        field("data.shippingInfo.zipCode", "우편번호", response.shippingInfo.zipCode)
                        field("data.shippingInfo.address1", "주소", response.shippingInfo.address1)
                        optionalField("data.shippingInfo.address2", "상세주소", response.shippingInfo.address2)
                        optionalField("data.shippingInfo.deliveryMessage", "배송메시지", response.shippingInfo.deliveryMessage)
                        field("data.orderedAt", "주문 날짜", response.orderedAt.toString())
                        optionalField("data.paidAt", "결제 날짜", response.paidAt.toString())
                        field("data.cancellable", "주문취소 가능여부", response.cancellable)
                        field("data.cancelRequested", "주문취소 여부", response.cancelRequested)
                        optionalField("data.cancelledAt", "주문취소 날짜", response.cancelledAt.toString())
                        field("data.refundable", "환불신청 가능여부", response.refundable)
                        field("data.refundRequested", "환불신청 여부", response.refundRequested)
                        optionalField("data.refundRequestedAt", "환불신청 날짜", response.refundRequestedAt.toString())
                        field("data.refunded", "환불승인 여부", response.refunded)
                        optionalField("data.refundedAt", "환불승인 날짜", response.refundedAt.toString())
                        ignoredField("error")
                    }
                }
            }
        }

        describe("DELETE /admin/orders/{orderId}/cancel - 주문 취소") {
            it("주문을 취소할 수 있다.") {
                val summary = "관리자 주문 취소"
                every { adminOrderService.cancelOrder(any()) } returns Unit
                documentation(
                    identifier = "관리자_주문취소_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.DELETE, "/admin/orders/{orderId}/cancel") {
                        pathVariable("orderId", "주문 아이디", 1)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    responseBody {
                        ignoredField("data")
                        ignoredField("error")
                    }
                }
            }
        }

        describe("PATCH /admin/orders/{orderId}/status/preparing-shipment - 배송준비중 상태 변경") {
            it("배송준비중 상태로 변경할 수 있다.") {
                val summary = "배송준비중 상태 변경"
                every { adminOrderService.preparingShipment(any(), any()) } returns Unit
                documentation(
                    identifier = "배송준비중_상태변경_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.PATCH, "/admin/orders/{orderId}/status/preparing-shipment") {
                        pathVariable("orderId", "주문 아이디", 1)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("trackingNumber", "운송장번호", "123981389")
                    }

                    responseBody {
                        ignoredField("data")
                        ignoredField("error")
                    }
                }
            }
        }

        describe("PATCH /admin/orders/{orderId}/status/shipped - 배송중 상태 변경") {
            it("배송중 상태로 변경할 수 있다.") {
                val summary = "배송중 상태 변경"
                every { adminOrderService.shippedOrder(any()) } returns Unit
                documentation(
                    identifier = "배송중_상태변경_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.PATCH, "/admin/orders/{orderId}/status/shipped") {
                        pathVariable("orderId", "주문 아이디", 1)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    responseBody {
                        ignoredField("data")
                        ignoredField("error")
                    }
                }
            }
        }

        describe("PATCH /admin/orders/{orderId}/status/delivered - 배송완료 상태 변경") {
            it("배송완료 상태로 변경할 수 있다.") {
                val summary = "배송완료 상태 변경"
                every { adminOrderService.deliveredOrder(any()) } returns Unit
                documentation(
                    identifier = "배송완료_상태변경_성공",
                    tag = tag,
                    summary = summary,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.PATCH, "/admin/orders/{orderId}/status/delivered") {
                        pathVariable("orderId", "주문 아이디", 1)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    responseBody {
                        ignoredField("data")
                        ignoredField("error")
                    }
                }
            }
        }
    }
}
