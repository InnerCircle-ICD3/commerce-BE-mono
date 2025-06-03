package com.fastcampus.commerce.file.application

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.domain.entity.FileMetadata
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.model.DomainType
import com.fastcampus.commerce.file.domain.model.FileType
import com.fastcampus.commerce.file.domain.service.FileStore
import com.fastcampus.commerce.file.domain.validator.FileUploadPolicyValidator
import com.fastcampus.commerce.file.infrastructure.s3.PresignedUrlGenerator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FileCommandServiceTest : FunSpec(
    {
        val fileUploadPolicyValidator = mockk<FileUploadPolicyValidator>(relaxed = true)
        val fileStore = mockk<FileStore>()
        val presignedUrlGenerator = mockk<PresignedUrlGenerator>()
        val service = FileCommandService(
            fileUploadPolicyValidator,
            fileStore,
            presignedUrlGenerator,
        )

        val uploaderId = 1L
        val contextKey = UUID.randomUUID()

        val request = GeneratePresignedUrlRequest(
            fileName = "coffee.jpg",
            contentType = "image/jpeg",
            fileSize = 1024,
            domainType = DomainType.PRODUCT,
            domainContext = "thumbnail",
            fileType = FileType.IMAGE,
            contextId = null,
        )

        val metadata = FileMetadata(
            contextKey = contextKey,
            storedPath = "product/$contextKey/thumbnail-uuid.jpg",
            storedFileName = "thumbnail-uuid.jpg",
            originalFileName = request.fileName,
            contentType = request.contentType,
            fileSize = request.fileSize,
        )

        val uploadUrl = "https://s3.com/presigned"

        beforeTest {
            clearMocks(fileUploadPolicyValidator, fileStore, presignedUrlGenerator)
        }

        test("Presigned URL 생성 흐름 전체를 정상 수행한다") {
            every { fileStore.prepareGeneratePresignedUrl(uploaderId, request) } returns metadata
            every { presignedUrlGenerator.generate(metadata) } returns uploadUrl

            val result = service.generatePresignedUrl(uploaderId, request)

            result.uploadUrl shouldBe uploadUrl
            result.key shouldBe metadata.storedPath
            result.contextId shouldBe metadata.contextKey

            verify(exactly = 1) { fileUploadPolicyValidator.validate(request) }
            verify(exactly = 1) { fileStore.prepareGeneratePresignedUrl(uploaderId, request) }
            verify(exactly = 1) { presignedUrlGenerator.generate(metadata) }
        }

        test("Validator에서 예외 발생 시 이후 단계는 호출되지 않는다") {
            every { fileUploadPolicyValidator.validate(request) } throws CoreException(FileErrorCode.FILE_NAME_EMPTY)

            shouldThrow<CoreException> {
                service.generatePresignedUrl(uploaderId, request)
            }.errorCode shouldBe FileErrorCode.FILE_NAME_EMPTY

            verify(exactly = 1) { fileUploadPolicyValidator.validate(request) }
            verify(exactly = 0) { fileStore.prepareGeneratePresignedUrl(any(), any()) }
            verify(exactly = 0) { presignedUrlGenerator.generate(any()) }
        }

        test("FileStore에서 예외 발생 시 generator는 호출되지 않는다") {
            every { fileUploadPolicyValidator.validate(request) } returns Unit
            every { fileStore.prepareGeneratePresignedUrl(uploaderId, request) } throws CoreException(FileErrorCode.S3_CLIENT_ERROR)

            shouldThrow<CoreException> {
                service.generatePresignedUrl(uploaderId, request)
            }.errorCode shouldBe FileErrorCode.S3_CLIENT_ERROR

            verify(exactly = 1) { fileUploadPolicyValidator.validate(request) }
            verify(exactly = 1) { fileStore.prepareGeneratePresignedUrl(uploaderId, request) }
            verify(exactly = 0) { presignedUrlGenerator.generate(any()) }
        }

        test("PresignedUrlGenerator에서 예외 발생 시 그대로 전파된다") {
            every { fileUploadPolicyValidator.validate(request) } returns Unit
            every { fileStore.prepareGeneratePresignedUrl(uploaderId, request) } returns metadata
            every { presignedUrlGenerator.generate(metadata) } throws CoreException(FileErrorCode.S3_SERVER_ERROR)

            shouldThrow<CoreException> {
                service.generatePresignedUrl(uploaderId, request)
            }.errorCode shouldBe FileErrorCode.S3_SERVER_ERROR

            verify(exactly = 1) { fileUploadPolicyValidator.validate(request) }
            verify(exactly = 1) { fileStore.prepareGeneratePresignedUrl(uploaderId, request) }
            verify(exactly = 1) { presignedUrlGenerator.generate(metadata) }
        }
    },
)
