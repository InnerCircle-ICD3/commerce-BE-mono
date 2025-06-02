package com.fastcampus.commerce.file.domain.validator

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FileNameValidatorTest : FunSpec(
    {
        val validator = FileNameValidator()

        context("성공") {
            test("정상적인 파일명은 유효성 검증을 통과한다.") {
                validator.validate("test.png")
            }

            test("파일명은 확장자를 포함해 최대 255자까지 가능하다.") {
                val extension = ".png"
                val availableLength = FileNameValidator.MAX_FILE_NAME_LENGTH - extension.length
                val fileName = "a".repeat(availableLength) + extension
                validator.validate(fileName)
            }
        }

        test("빈 파일명은 FILE_NAME_EMPTY 예외가 발생한다.") {
            shouldThrow<CoreException> {
                validator.validate(" ")
            }.errorCode shouldBe FileErrorCode.FILE_NAME_EMPTY
        }

        test("..이 포함된 파일명은 INVALID_FILE_NAME 예외가 발생한다.") {
            shouldThrow<CoreException> {
                validator.validate("test..png")
            }.errorCode shouldBe FileErrorCode.INVALID_FILE_NAME
        }

        test("파일명의 길이가 확장자를 포함해 255자를 초과하면 FILE_NAME_TOO_LONG 예외가 발생한다.") {
            val extension = ".png"
            val availableLength = FileNameValidator.MAX_FILE_NAME_LENGTH - extension.length
            val fileName = "a".repeat(availableLength + 1) + extension
            shouldThrow<CoreException> {
                validator.validate(fileName)
            }.errorCode shouldBe FileErrorCode.FILE_NAME_TOO_LONG
        }
    },
)
