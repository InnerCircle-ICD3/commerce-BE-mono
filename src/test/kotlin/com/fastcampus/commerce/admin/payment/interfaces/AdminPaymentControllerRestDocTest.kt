package com.fastcampus.commerce.admin.payment.interfaces

import com.fastcampus.commerce.admin.payment.application.AdminPaymentService
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
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@WebMvcTest(AdminPaymentController::class)
@Import(TestConfig::class)
class AdminPaymentControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminPaymentService: AdminPaymentService

    val tag = "Admin-Payment"
    val privateResource = true

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("POST /admin/payments/refund/approve") {
            val summary = "관리자 환불승인"
            val description = """
                PAY-002: 결제 정보를 찾을 수 없습니다.
                ORD-001: 주문 내역을 찾을 수 없습니다.
                ORD-104: 환불요청한 주문만 환불가능합니다.
            """.trimMargin()
            it("환불을 승인할 수 있다.") {
                every { adminPaymentService.refundApprove(any(), any()) } returns Unit

                documentation(
                    identifier = "관리자_환불승인_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.POST, "/admin/payments/refund/approve")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("paymentNumber", "결제 번호", "PAY00001")
                    }

                    responseBody {
                        field("data.message", "응답 메시지", "OK")
                        ignoredField("error")
                    }
                }
            }
        }

        describe("POST /admin/payments/refund/reject") {
            val summary = "관리자 환불거절"
            val description = """
                PAY-002: 결제 정보를 찾을 수 없습니다.
                ORD-001: 주문 내역을 찾을 수 없습니다.
                ORD-105: 환불요청한 주문만 환불거절 가능합니다.
            """.trimMargin()
            it("환불을 거절할 수 있다.") {
                every { adminPaymentService.refundReject(any(), any()) } returns Unit

                documentation(
                    identifier = "관리자_환불거절_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                    privateResource = privateResource,
                ) {
                    requestLine(HttpMethod.POST, "/admin/payments/refund/reject")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("paymentNumber", "결제 번호", "PAY00001")
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
