package com.fastcampus.commerce.common.error

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.slf4j.Logger
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException

class GlobalExceptionHandlerTest : FunSpec(
    {
        val mockLogger = mockk<Logger>(relaxed = true)
        val handler = GlobalExceptionHandler(log = mockLogger)

        beforeEach {
            clearMocks(mockLogger)
        }

        context("CoreException 처리 시") {
            test("ApiResponse.error 객체를 반환한다.") {
                val expectedCode = CommonErrorCode.SERVER_ERROR
                val expectedError = ErrorMessage(expectedCode)
                val exception = CoreException(expectedCode)

                val sut = handler.handleCoreException(exception)

                sut.data shouldBe null
                sut.error shouldBe expectedError
                sut.error?.code shouldBe expectedError.code
                sut.error?.message shouldBe expectedError.message
            }

            test("ERROR 레벨 로깅이 되어야 한다.") {
                val errorCode = object : ErrorCode {
                    override val code: String = "TST-001"
                    override val message: String = "테스트 에러"
                    override val logLevel: LogLevel = LogLevel.ERROR
                }
                val exception = CoreException(errorCode)

                handler.handleCoreException(exception)

                verify { mockLogger.error("CoreException: {}", exception.message, exception) }
            }

            test("WARN 레벨 로깅이 되어야 한다.") {
                val errorCode = object : ErrorCode {
                    override val code: String = "TST-001"
                    override val message: String = "테스트 에러"
                    override val logLevel: LogLevel = LogLevel.WARN
                }
                val exception = CoreException(errorCode)

                handler.handleCoreException(exception)

                verify { mockLogger.warn("CoreException: {}", exception.message, exception) }
            }

            test("INFO 레벨 로깅이 되어야 한다.") {
                val errorCode = object : ErrorCode {
                    override val code: String = "TST-001"
                    override val message: String = "테스트 에러"
                    override val logLevel: LogLevel = LogLevel.INFO
                }
                val exception = CoreException(errorCode)

                handler.handleCoreException(exception)

                verify { mockLogger.info("CoreException: {}", exception.message, exception) }
            }
        }

        context("BindException 처리 시") {
            test("ApiResponse.error 객체를 반환한다.") {
                val expectedCode = CommonErrorCode.FIELD_ERROR
                val expectedMessage = "이름이 비어있습니다."
                val expectedError = ErrorMessage(expectedCode, expectedMessage)

                val bindingResult = mockk<BindingResult>()
                val fieldError = FieldError("testObject", "name", expectedMessage)
                every { bindingResult.fieldErrors } returns listOf(fieldError)
                val exception = BindException(bindingResult)

                val sut = handler.handleBindException(exception)

                sut.data shouldBe null
                sut.error shouldBe expectedError
                sut.error?.code shouldBe expectedError.code
                sut.error?.message shouldBe expectedError.message

                verify { mockLogger.error("BindException: {}", exception.message, exception) }
            }
        }

        context("ConstraintViolationException 처리 시") {
            test("ApiResponse.error 객체를 반환한다.") {
                val expectedCode = CommonErrorCode.FIELD_ERROR
                val expectedMessage = "이름이 비어있습니다."
                val expectedError = ErrorMessage(expectedCode, expectedMessage)

                val constraintViolation = mockk<ConstraintViolation<*>>()
                every { constraintViolation.message } returns expectedMessage
                every { constraintViolation.propertyPath.toString() } returns "name"
                val exception = ConstraintViolationException(setOf(constraintViolation))

                val sut = handler.handleConstraintViolation(exception)

                sut.data shouldBe null
                sut.error shouldBe expectedError
                sut.error?.code shouldBe expectedError.code
                sut.error?.message shouldBe expectedError.message

                verify { mockLogger.error("ConstraintViolationException: {}", exception.message, exception) }
            }
        }

        context("CoreException을 제외한 Exception 처리 시") {
            test("기본 에러 응답과 ERROR 로깅이 되어야 한다.") {
                val expectedError = ErrorMessage(CommonErrorCode.SERVER_ERROR)
                val expectedLoggingMessage = "알 수 없는 오류"
                val exception = RuntimeException(expectedLoggingMessage)

                val sut = handler.handleException(exception)

                sut.data shouldBe null
                sut.error shouldBe expectedError
                sut.error?.code shouldBe expectedError.code
                sut.error?.message shouldBe expectedError.message

                verify { mockLogger.error("Exception: {}", exception.message, exception) }
            }
        }

        context("로그 호출 횟수 검증") {
            test("예외가 발생한 만큼 로그가 호출되어야 한다.") {
                repeat(3) {
                    handler.handleException(RuntimeException("Error $it"))
                }

                verify(exactly = 3) {
                    mockLogger.error(
                        "Exception: {}",
                        match { message: String -> message.startsWith("Error ") },
                        ofType<RuntimeException>(),
                    )
                }
            }
        }
    },
)
