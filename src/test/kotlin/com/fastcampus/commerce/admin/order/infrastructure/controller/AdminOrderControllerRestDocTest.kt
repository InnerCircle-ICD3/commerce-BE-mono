package com.fastcampus.commerce.admin.order.infrastructure.controller

import com.fastcampus.commerce.admin.order.application.AdminOrderService
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderDetailItemResponse
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderDetailResponse
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderListResponse
import com.fastcampus.commerce.admin.payment.application.AdminPaymentService
import com.fastcampus.commerce.admin.payment.interfaces.AdminPaymentController
import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.restdoc.documentation
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                        productName = "상품",
                        productQuantity = 10,
                        productUnitPrice = 1000,
                        orderDate = orderDate,
                        customerName = "홍길동",
                        totalAmount = 10000,
                        paymentDate = orderDate,
                        status = "배송중"
                    )
                )
                val response = PageImpl(searchResponse, PageRequest.of(0, 10), 1L)
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
                        optionalField("keyword", "주문번호, 고객명, 상품명 등 검색키워드", "콜드")
                        optionalField("status", "주문상태", "배송중")
                        optionalField("dateFrom", "주문날짜 From", "2025.01.01")
                        optionalField("dateTo", "주문날짜 To", "2025.03.01")
                        optionalField("page", "페이지 번호 (1부터 시작, 기본값: 1)", 1)
                    }

                    responseBody {
                        field("data.content[0].orderId", "주문 ID", searchResponse[0].orderId.toInt())
                        field("data.content[0].orderNumber", "주문번호", searchResponse[0].orderNumber)
                        field("data.content[0].productName", "대표 상품명", searchResponse[0].productName)
                        field("data.content[0].productQuantity", "", searchResponse[0].productQuantity)
                        field("data.content[0].productUnitPrice", "판매상태", searchResponse[0].productUnitPrice)
                        field("data.content[0].orderDate", "재고", "2025-06-10T12:00")
                        field("data.content[0].customerName", "원두 강도", searchResponse[0].customerName)
                        field("data.content[0].totalAmount", "컵 사이즈", searchResponse[0].totalAmount)
                        field("data.content[0].paymentDate", "컵 사이즈", "2025-06-10T12:00")
                        field("data.content[0].status", "컵 사이즈", searchResponse[0].status)
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
                    orderNumber = "ORD123",
                    status = "배송중",
                    createdAt = orderDate,
                    paymentMethod = "MOCK",
                    address = "서울시 관악구",
                    recipientName = "홍길동",
                    recipientPhone = "0101234",
                    customerName = "커피매니아",
                    customerEmail = "커피매니아@mail.com",
                    items = listOf(
                        AdminOrderDetailItemResponse(
                            productName = "스타벅스 캡슐",
                            quantity = 1,
                            price = 1000,
                            total = 1000,
                            thumbnail = "http://asdf.com?thumb.jpg",
                        )
                    ),
                    subtotal = 1000,
                    total = 1000,
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
                        field("data.orderNumber", "주문번호", response.orderNumber)
                        field("data.status", "주문번호", response.status)
                        field("data.createdAt", "주문번호", "2025-06-10T12:00")
                        field("data.paymentMethod", "주문번호", response.paymentMethod)
                        field("data.address", "주문번호", response.address)
                        field("data.recipientName", "주문번호", response.recipientName)
                        field("data.recipientPhone", "주문번호", response.recipientPhone)
                        field("data.customerName", "주문번호", response.customerName)
                        field("data.customerEmail", "주문번호", response.customerEmail)
                        field("data.items[0].productName", "주문번호", response.items[0].productName)
                        field("data.items[0].quantity", "주문번호", response.items[0].quantity)
                        field("data.items[0].price", "주문번호", response.items[0].price)
                        field("data.items[0].total", "주문번호", response.items[0].total)
                        optionalField("data.items[0].thumbnail", "주문번호", response.items[0].thumbnail)
                        field("data.subtotal", "주문번호", response.subtotal)
                        field("data.total", "주문번호", response.total)
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
    }
}
