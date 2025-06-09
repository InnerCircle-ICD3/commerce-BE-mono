package com.fastcampus.commerce.user.api.controller

import com.fastcampus.commerce.config.TestSecurityConfig
import com.fastcampus.commerce.restdoc.documentation
import com.fastcampus.commerce.user.api.service.UserAddressService
import com.fastcampus.commerce.user.api.service.response.UserAddressResponse
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
@WebMvcTest(UserAddressController::class)
@Import(TestSecurityConfig::class)
class UserAddressControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var userAddressService: UserAddressService

    val tag = "Address"

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("GET /users/addresses") {
            val summary = "유저 배송지 목록조회"
            it("배송지 목록을 조회할 수 있다") {
                val addresses = listOf(
                    UserAddressResponse(
                        addressId = 1L,
                        alias = "집",
                        recipientName = "홍길동",
                        recipientPhone = "010-1234-5678",
                        zipCode = "12345",
                        address1 = "서울시 강남구 테헤란로 123",
                        address2 = "A동 101호",
                        isDefault = true,
                    ),
                )

                every { userAddressService.getUserAddresses(any()) } returns addresses

                documentation(
                    identifier = "배송지_목록조회_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/users/addresses")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    responseBody {
                        field("data[0].addressId", "배송지 ID", 1)
                        field("data[0].alias", "배송지 별칭", "집")
                        field("data[0].recipientName", "수령인 이름", "홍길동")
                        field("data[0].recipientPhone", "수령인 전화번호", "010-1234-5678")
                        field("data[0].zipCode", "우편번호", "12345")
                        field("data[0].address1", "기본 주소", "서울시 강남구 테헤란로 123")
                        optionalField("data[0].address2", "상세 주소", "A동 101호")
                        field("data[0].isDefault", "기본 배송지 여부", true)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("POST /users/addresses") {
            val summary = "유저 배송지 등록"
            it("배송지를 등록할 수 있다") {
                every { userAddressService.register(any(), any()) } returns 10L

                documentation(
                    identifier = "배송지_등록_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.POST, "/users/addresses")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("alias", "배송지 별칭", "회사")
                        field("recipientName", "수신자", "홍길동")
                        field("recipientPhone", "수신자 연락처", "010-1234-1234")
                        field("zipCode", "우편번호", "08123")
                        field("address1", "주소", "서울특별시 관악구")
                        optionalField("address2", "상세주소", "123동 123호")
                        optionalField("isDefault", "기본 배송지 여부(기본값: false)", true)
                    }

                    responseBody {
                        field("data.addressId", "배송지 아이디", 10)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("PUT /users/addresses/{addressId}") {
            val summary = "유저 배송지 수정"
            it("배송지 정보를 수정할 수 있다.") {
                every { userAddressService.update(any(), any(), any()) } just Runs

                documentation(
                    identifier = "배송지_수정_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.PUT, "/users/addresses/{addressId}") {
                        pathVariable("addressId", "배송지 아이디", 10)
                    }

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("alias", "배송지 별칭", "회사")
                        field("recipientName", "수신자", "홍길동")
                        field("recipientPhone", "수신자 연락처", "010-1234-1234")
                        field("zipCode", "우편번호", "08123")
                        field("address1", "주소", "서울특별시 관악구")
                        optionalField("address2", "상세주소", "123동 123호")
                        optionalField("isDefault", "기본 배송지 여부(기본값: false)", true)
                    }

                    responseBody {
                        field("data.addressId", "배송지 아이디", 10)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("DELETE /users/addresses/{addressId}") {
            val summary = "유저 배송지 삭제"
            it("배송지 정보를 삭제할 수 있다.") {
                every { userAddressService.delete(any(), any()) } just Runs

                documentation(
                    identifier = "배송지_삭제_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.DELETE, "/users/addresses/{addressId}") {
                        pathVariable("addressId", "배송지 아이디", 10)
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
