package com.fastcampus.commerce.common.error

import com.fastcampus.commerce.common.response.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import jakarta.validation.ConstraintViolationException

@RestControllerAdvice
class GlobalExceptionHandler(
    private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java),
) {
    @ExceptionHandler(CoreException::class)
    fun handleCoreException(e: CoreException): ApiResponse<Nothing?> {
        when (e.errorCode.logLevel) {
            LogLevel.ERROR -> log.error("CoreException: {}", e.message, e)
            LogLevel.WARN -> log.warn("CoreException: {}", e.message, e)
            LogLevel.INFO -> log.info("CoreException: {}", e.message, e)
        }
        return ApiResponse.error(ErrorMessage(e.errorCode))
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): ApiResponse<Nothing?> {
        log.error("BindException: {}", e.message, e)
        /* TODO: 프론트와 에러 형식에 대해 상의 후 어떤 코드를 사용할지 결정한다.
        val fieldErrors = e.bindingResult.fieldErrors.map {
            FieldError(it.field, it.defaultMessage ?: "Invalid input")
        }
        return ApiResponse.error(ErrorMessage(CommonErrorCode.FIELD_ERROR, fieldErrors))
         */
        val message = e.bindingResult.fieldErrors.first().defaultMessage
        return ApiResponse.error(ErrorMessage(CommonErrorCode.FIELD_ERROR, message))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ApiResponse<Nothing?> {
        log.error("ConstraintViolationException: {}", e.message, e)
        val message = e.constraintViolations.firstOrNull()?.message ?: "유효하지 않은 값입니다."
        return ApiResponse.error(ErrorMessage(CommonErrorCode.FIELD_ERROR, message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ApiResponse<Nothing?> {
        log.error("Exception: {}", e.message, e)
        return ApiResponse.error(ErrorMessage(CommonErrorCode.SERVER_ERROR))
    }
}
