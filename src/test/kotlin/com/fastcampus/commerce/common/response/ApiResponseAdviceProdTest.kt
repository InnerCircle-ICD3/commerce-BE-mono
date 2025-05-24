package com.fastcampus.commerce.common.response

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

class ApiResponseAdviceTest : FunSpec({

    test("Spring Profile이 dev, test, local 일 때, String을 반환하면 IllegalStateException 예외를 던진다") {
        val env = mockk<Environment> {
            every { activeProfiles } returns arrayOf("dev")
        }
        val advice = ApiResponseAdvice(env)

        shouldThrow<IllegalStateException> {
            advice.beforeBodyWrite(
                body = "hello world",
                returnType = mockk(),
                selectedContentType = MediaType.APPLICATION_JSON,
                selectedConverterType = MappingJackson2HttpMessageConverter::class.java,
                request = mockk(),
                response = mockk(),
            )
        }
    }

    test("Spring Profile이 prod 일 때, String을 반환해도 예외가 발생하지 않는다") {
        val env = mockk<Environment> {
            every { activeProfiles } returns arrayOf("prod")
        }
        val advice = ApiResponseAdvice(env)

        val sut = advice.beforeBodyWrite(
            body = "hello world",
            returnType = mockk(),
            selectedContentType = MediaType.APPLICATION_JSON,
            selectedConverterType = MappingJackson2HttpMessageConverter::class.java,
            request = mockk(),
            response = mockk(),
        )

        sut shouldBe ApiResponse.success("hello world")
    }

    test("객체를 반환하면 ApiResponse로 감싸진다") {
        val advice = ApiResponseAdvice(mockk())
        val body = mapOf("hello" to "world")

        val sut = advice.beforeBodyWrite(
            body = body,
            returnType = mockk(),
            selectedContentType = MediaType.APPLICATION_JSON,
            selectedConverterType = MappingJackson2HttpMessageConverter::class.java,
            request = mockk(),
            response = mockk(),
        )

        sut shouldBe ApiResponse.success(body)
    }

    test("ApiResponse 객체를 반환하면 그대로 응답된다") {
        val advice = ApiResponseAdvice(mockk())
        val apiResponse = ApiResponse.success(mapOf("hello" to "world"))

        val sut = advice.beforeBodyWrite(
            body = apiResponse,
            returnType = mockk(),
            selectedContentType = MediaType.APPLICATION_JSON,
            selectedConverterType = MappingJackson2HttpMessageConverter::class.java,
            request = mockk(),
            response = mockk(),
        )

        sut shouldBe apiResponse
    }
})
