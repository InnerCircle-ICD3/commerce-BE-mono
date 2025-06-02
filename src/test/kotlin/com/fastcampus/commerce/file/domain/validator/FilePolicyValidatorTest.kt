package com.fastcampus.commerce.file.domain.validator

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.model.DomainType
import com.fastcampus.commerce.file.domain.model.FilePolicy
import com.fastcampus.commerce.file.domain.model.FileType
import com.fastcampus.commerce.file.domain.service.FilePolicyProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class FilePolicyValidatorTest : FunSpec(
    {
        val filePolicyProvider = mockk<FilePolicyProvider>()
        val validator = FilePolicyValidator(filePolicyProvider)

        val validRequest = GeneratePresignedUrlRequest(
            fileName = "coffee.jpg",
            contentType = "image/jpeg",
            fileSize = 1,
            domainType = DomainType.PRODUCT,
            domainContext = "thumbnail",
            fileType = FileType.IMAGE,
            contextId = null,
        )

        val validPolicy = FilePolicy(
            maxSize = validRequest.fileSize + 1,
            allowedExtensions = setOf("jpg", "jpeg", "png"),
        )

        beforeTest {
            every {
                filePolicyProvider.resolve(any(), any(), any())
            } returns validPolicy
        }

        test("정상적인 파일은 유효성 검증을 통과한다.") {
            validator.validate(validRequest)
        }

        test("허용되는 파일 크기를 초과하면 FILE_SIZE_TOO_LARGE 예외가 발생한다.") {
            val overFileSize = validPolicy.maxSize + 1
            val largeFileRequest = validRequest.copy(fileSize = overFileSize)

            shouldThrow<CoreException> {
                validator.validate(largeFileRequest)
            }.errorCode shouldBe FileErrorCode.FILE_SIZE_TOO_LARGE
        }

        test("허용되지 않은 확장자는 예외 발생") {
            val invalidFileName = validRequest.fileName + "invalid"
            val invalidExtensionRequest = validRequest.copy(fileName = invalidFileName)

            shouldThrow<CoreException> {
                validator.validate(invalidExtensionRequest)
            }.errorCode shouldBe FileErrorCode.INVALID_FILE_TYPE
        }
    },
)
