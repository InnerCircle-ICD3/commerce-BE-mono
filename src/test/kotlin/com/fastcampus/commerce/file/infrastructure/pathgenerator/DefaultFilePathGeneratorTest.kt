package com.fastcampus.commerce.file.infrastructure.pathgenerator

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.model.DomainType
import com.fastcampus.commerce.file.domain.model.FileType
import com.fastcampus.commerce.file.domain.repository.UniqueIdGenerator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.Locale

class DefaultFilePathGeneratorTest : FunSpec(
    {
        val idGenerator = mockk<UniqueIdGenerator>()
        val generator = DefaultFilePathGenerator(idGenerator)

        val extension = "jpg"
        val request = GeneratePresignedUrlRequest(
            fileName = "coffee.${extension.uppercase(Locale.getDefault())}",
            contentType = "image/jpeg",
            fileSize = 1024,
            domainType = DomainType.PRODUCT,
            domainContext = "thumbnail",
            fileType = FileType.IMAGE,
            contextId = null,
        )

        test("파일 저장 경로와 저장될 파일명을 생성할 수 있다.") {
            val fixedUuid = "801base"
            every { idGenerator.generate() } returns fixedUuid

            val contextKeyId = "1234-asdf-zxcv-qwer"

            val filePath = generator.generate(contextKeyId, request)

            filePath.fileName shouldBe "${request.domainContext}-$fixedUuid.$extension"
            filePath.path shouldBe "${request.domainType.domainName}/$contextKeyId/${filePath.fileName}"
        }

        test("유효하지 않은 파일명으로 파일경로 객체를 생성하려고하면 INVALID_FILE_NAME 예외가 발생한다.") {
            val fixedUuid = "801base"
            every { idGenerator.generate() } returns fixedUuid

            val contextKeyId = "1234-asdf-zxcv-qwer"
            val invalidRequest = request.copy(fileName = "invalid")

            shouldThrow<CoreException> {
                generator.generate(contextKeyId, invalidRequest)
            }.errorCode shouldBe FileErrorCode.INVALID_FILE_NAME
        }
    },
)
