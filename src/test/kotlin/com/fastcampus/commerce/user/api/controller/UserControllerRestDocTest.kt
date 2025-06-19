package com.fastcampus.commerce.user.api.controller

import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.restdoc.documentation
import com.fastcampus.commerce.user.api.controller.request.MyInfoResponse
import com.fastcampus.commerce.user.api.service.UserService
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
@WebMvcTest(UserController::class)
@Import(TestConfig::class)
class UserControllerRestDocTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var userService: UserService

    val tag = "User"

    init {
        beforeSpec {
            RestAssuredMockMvc.mockMvc(mockMvc)
        }

        describe("GET /user/me - 내 정보조회") {
            val summary = "내 정보조회"
            it("내 정보를 조회할 수 있다.") {
                val response = MyInfoResponse(
                    name = "홍길동",
                    email = "길동@닷.컴",
                    nickname = "동길홍ㅋㅋ",
                )
                every { userService.getMyInfo(any()) } returns response

                documentation(
                    identifier = "내_정보조회_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.GET, "/user/me")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    responseBody {
                        field("data.name", "이름", response.name)
                        field("data.email", "이메일", response.email)
                        field("data.nickname", "닉네임", response.nickname)
                        ignoredField("error")
                    }
                }
            }
        }

        describe("PATCH /user/me - 내 정보수정") {
            val summary = "내 정보수정"
            it("내 정보를 수정할 수 있다.") {
                every { userService.updateMyInfo(any(), any()) } returns Unit

                documentation(
                    identifier = "내_정보수정_성공",
                    tag = tag,
                    summary = summary,
                ) {
                    requestLine(HttpMethod.PATCH, "/user/me")

                    requestHeaders {
                        header(HttpHeaders.AUTHORIZATION, "Authorization", "Bearer sample-token")
                    }

                    requestBody {
                        field("nickname", "닉네임", "하하하")
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
