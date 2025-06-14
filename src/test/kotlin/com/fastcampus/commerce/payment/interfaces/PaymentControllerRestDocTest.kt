package com.fastcampus.commerce.payment.interfaces

import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.payment.application.PaymentService
import com.fastcampus.commerce.payment.application.response.PaymentProcessResponse
import com.fastcampus.commerce.restdoc.documentation
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
@WebMvcTest(PaymentController::class)
@Import(TestConfig::class)
class PaymentControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var paymentService: PaymentService

    val tag = "Payment"

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("POST /payments") {
            val summary = "결제 처리 한다."
            val description = """
                ORD-001: 주문 내역을 찾을 수 없습니다.
                PAY-001: PG사 결제정보를 찾을 수 업습니다.
                PAY-002: 결제 정보를 찾을 수 없습니다.
                PAY-003: 이미 결제처리한 주문입니다.
                PAY-004: 이미 결제되었습니다.
                PAY-005: 결제금액이 일치하지 않습니다.
            """.trimMargin()
            it("결제처리할 수 있다.") {
                val paymentNumber = "PAY00001"
                val response = PaymentProcessResponse(paymentNumber)
                every { paymentService.processPayment(any()) } returns response

                documentation(
                    identifier = "결제처리_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                ) {
                    requestLine(HttpMethod.POST, "/payments")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("orderNumber", "주분번호", "ORD123123123")
                        field("transactionId", "PG사 결제 아이디", "12312312-123")
                    }

                    responseBody {
                        field("data.paymentNumber", "결제번호", paymentNumber)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("POST /payments/cancel") {
            val summary = "결제 취소 한다."
            val description = """
                ORD-001: 주문 내역을 찾을 수 없습니다.
                PAY-001: PG사 결제정보를 찾을 수 업습니다.
                PAY-002: 결제 정보를 찾을 수 없습니다.
                PAY-006: 결제 대기중, 결제 완료 상태인 주문만 취소할 수 있습니다.
                PAY-007: 다른 사람의 결제를 취소할 수 없습니다.
                PAY-008: PG사 결제 아이디가 누락되었습니다.
            """.trimMargin()
            it("결제취소할 수 있다.") {
                val paymentNumber = "PAY00001"
                every { paymentService.cancelPayment(any(), any()) } just Runs

                documentation(
                    identifier = "결제취소_성공",
                    tag = tag,
                    summary = summary,
                    description = description,
                ) {
                    requestLine(HttpMethod.POST, "/payments/cancel")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("orderNumber", "주분번호", "ORD123123123")
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
