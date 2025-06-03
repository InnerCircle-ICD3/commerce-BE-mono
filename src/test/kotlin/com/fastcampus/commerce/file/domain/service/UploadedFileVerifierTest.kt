package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.entity.FileMetadata
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.model.ActualFile
import com.fastcampus.commerce.file.infrastructure.s3.S3FileReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk

class UploadedFileVerifierTest : FunSpec(
    {

        val fileReader = mockk<FileReader>()
        val s3FileReader = mockk<S3FileReader>()
        val verifier = UploadedFileVerifier(
            fileReader = fileReader,
            s3FileReader = s3FileReader,
        )

        beforeTest {
            clearMocks(
                fileReader,
                s3FileReader,
            )
        }

        test("업로드 URL로 실제 업로드된 파일과 요청당시의 파일을 비교할 수 있다.") {
            val fileSize = 1024
            val contentType = "image/png"
            val storedPath = "product/uuid/test.png"
            val uploadUrl = "https://test-bucket.amazon.com/$storedPath"
            every { fileReader.getFileMetadataByStoredPath(any()) } returns FileMetadata(
                contextKey = mockk(),
                storedPath = storedPath,
                storedFileName = "test.png",
                originalFileName = "test.png",
                contentType = contentType,
                fileSize = fileSize,
            )
            every { s3FileReader.read(any()) } returns ActualFile(
                size = fileSize.toLong(),
                contentType = contentType,
            )

            verifier.verifyFileWithS3Urls(listOf(uploadUrl))
        }
        test("업로드된 파일과 요청당시의 파일의 파일 크기가 다르면 FILE_NOT_MATCH 예외가 발생한다.") {
            val requestFileSize = 1024
            val uploadFileSize = requestFileSize + 1
            val contentType = "image/png"
            val storedPath = "product/uuid/test.png"
            val uploadUrl = "https://test-bucket.amazon.com/$storedPath"
            every { fileReader.getFileMetadataByStoredPath(any()) } returns FileMetadata(
                contextKey = mockk(),
                storedPath = storedPath,
                storedFileName = "test.png",
                originalFileName = "test.png",
                contentType = contentType,
                fileSize = requestFileSize,
            )
            every { s3FileReader.read(any()) } returns ActualFile(
                size = uploadFileSize.toLong(),
                contentType = contentType,
            )

            shouldThrow<CoreException> {
                verifier.verifyFileWithS3Urls(listOf(uploadUrl))
            }.errorCode shouldBe FileErrorCode.FILE_NOT_MATCH
        }

        test("업로드된 파일과 요청당시의 파일의 Conent-Type이 다르면 FILE_NOT_MATCH 예외가 발생한다.") {
            val fileSize = 1024
            val requestContentType = "image/png"
            val uploadContentType = requestContentType + "a"
            val storedPath = "product/uuid/test.png"
            val uploadUrl = "https://test-bucket.amazon.com/$storedPath"
            every { fileReader.getFileMetadataByStoredPath(any()) } returns FileMetadata(
                contextKey = mockk(),
                storedPath = storedPath,
                storedFileName = "test.png",
                originalFileName = "test.png",
                contentType = requestContentType,
                fileSize = fileSize,
            )
            every { s3FileReader.read(any()) } returns ActualFile(
                size = fileSize.toLong(),
                contentType = uploadContentType,
            )

            shouldThrow<CoreException> {
                verifier.verifyFileWithS3Urls(listOf(uploadUrl))
            }.errorCode shouldBe FileErrorCode.FILE_NOT_MATCH
        }
    },
)
