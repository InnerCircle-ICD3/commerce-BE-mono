package com.fastcampus.commerce.file.infrastructure.s3

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import software.amazon.awssdk.awscore.exception.AwsErrorDetails
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectResponse
import software.amazon.awssdk.services.s3.model.S3Exception

class S3FileReaderTest : FunSpec(
    {

        val s3Client = mockk<S3Client>()
        val bucket = "test-bucket"
        val s3FileReader = S3FileReader(
            bucket = bucket,
            s3Client = s3Client,
        )

        beforeTest {
            clearMocks(s3Client)
        }

        val storedPath = "product/uuid/test.png"
        test("업로드된 파일의 메타데이터를 조회할 수 있다.") {
            val fileSize = 1024L
            val contentType = "image/png"
            val mockResponse = mockk<HeadObjectResponse> {
                every { contentLength() } returns fileSize
                every { contentType() } returns contentType
            }
            every { s3Client.headObject(any<HeadObjectRequest>()) } returns mockResponse

            val result = s3FileReader.read(storedPath)

            result.size shouldBe fileSize
            result.contentType shouldBe contentType
        }

        test("SdkClientException 발생 시 로깅 후 S3_CLIENT_ERROR 예외를 던진다.") {
            every {
                s3Client.headObject(any<HeadObjectRequest>())
            } throws SdkClientException.builder()
                .message("connection fail")
                .build()

            shouldThrow<CoreException> {
                s3FileReader.read(storedPath)
            }.errorCode shouldBe FileErrorCode.S3_CLIENT_ERROR
        }

        context("S3Exception 예외 발생 시 에러코드가") {
            test("NoSuchBucket면 S3_BUCKET_NOT_FOUND 예외를 던진다.") {
                val e = S3Exception.builder()
                    .message("no bucket")
                    .statusCode(404)
                    .awsErrorDetails(AwsErrorDetails.builder().errorCode("NoSuchBucket").build())
                    .build()
                every { s3Client.headObject(any<HeadObjectRequest>()) } throws e

                shouldThrow<CoreException> {
                    s3FileReader.read(storedPath)
                }.errorCode shouldBe FileErrorCode.S3_BUCKET_NOT_FOUND
            }

            test("AccessDenied면 S3_ACCESS_DENIED 예외를 던진다.") {
                val e = S3Exception.builder()
                    .message("no permission")
                    .statusCode(403)
                    .awsErrorDetails(AwsErrorDetails.builder().errorCode("AccessDenied").build())
                    .build()
                every { s3Client.headObject(any<HeadObjectRequest>()) } throws e

                shouldThrow<CoreException> {
                    s3FileReader.read(storedPath)
                }.errorCode shouldBe FileErrorCode.S3_ACCESS_DENIED
            }

            test("NoSuchBucket, AccessDenied가 아니라면 S3_SERVER_ERROR 예외를 던진다.") {
                val e = S3Exception.builder()
                    .message("boom")
                    .statusCode(500)
                    .awsErrorDetails(AwsErrorDetails.builder().errorCode("BoomError").build())
                    .build()
                every { s3Client.headObject(any<HeadObjectRequest>()) } throws e

                shouldThrow<CoreException> {
                    s3FileReader.read(storedPath)
                }.errorCode shouldBe FileErrorCode.S3_SERVER_ERROR
            }
        }

        test("Exception 발생 시 로깅 후 FAIL_GENERATE_PRESIGNED_URL 예외를 던진다.") {
            every { s3Client.headObject(any<HeadObjectRequest>()) } throws RuntimeException("unexpected")

            shouldThrow<CoreException> {
                s3FileReader.read(storedPath)
            }.errorCode shouldBe FileErrorCode.FAIL_GENERATE_PRESIGNED_URL
        }
    },
)
