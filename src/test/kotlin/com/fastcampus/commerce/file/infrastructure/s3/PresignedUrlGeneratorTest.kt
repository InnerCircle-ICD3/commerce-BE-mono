package com.fastcampus.commerce.file.infrastructure.s3

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.entity.FileMetadata
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import software.amazon.awssdk.awscore.exception.AwsErrorDetails
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URI
import java.util.UUID

class PresignedUrlGeneratorTest : FunSpec(
    {
        val presigner = mockk<S3Presigner>()
        val bucket = "test-bucket"
        val ttlMs = 60000L
        val generator = PresignedUrlGenerator(
            bucket = bucket,
            ttlMs = ttlMs,
            presigner = presigner,
        )

        val metadata = FileMetadata(
            contextKey = UUID.randomUUID(),
            storedPath = "product/uuid/file.jpg",
            storedFileName = "file.jpg",
            originalFileName = "file.jpg",
            contentType = "image/jpeg",
            fileSize = 1234,
        )

        val urlPrefix = "https://s3.test.com"
        val fakeUrl = URI.create("$urlPrefix/${metadata.storedPath}").toURL()

        test("Presigned URL을 성공적으로 반환한다") {
            val mockResponse = mockk<PresignedPutObjectRequest> {
                every { url() } returns fakeUrl
            }
            every { presigner.presignPutObject(any<PutObjectPresignRequest>()) } returns mockResponse

            val result = generator.generate(metadata)

            result shouldBe fakeUrl.toString()
        }

        test("SdkClientException 발생 시 로깅 후 S3_CLIENT_ERROR 예외를 던진다.") {
            every {
                presigner.presignPutObject(any<PutObjectPresignRequest>())
            } throws SdkClientException.builder()
                .message("connection fail")
                .build()

            shouldThrow<CoreException> {
                generator.generate(metadata)
            }.errorCode shouldBe FileErrorCode.S3_CLIENT_ERROR
        }

        context("S3Exception 예외 발생 시 에러코드가") {
            test("NoSuchBucket면 S3_BUCKET_NOT_FOUND 예외를 던진다.") {
                val e = S3Exception.builder()
                    .message("no bucket")
                    .statusCode(404)
                    .awsErrorDetails(AwsErrorDetails.builder().errorCode("NoSuchBucket").build())
                    .build()
                every { presigner.presignPutObject(any<PutObjectPresignRequest>()) } throws e

                shouldThrow<CoreException> {
                    generator.generate(metadata)
                }.errorCode shouldBe FileErrorCode.S3_BUCKET_NOT_FOUND
            }

            test("AccessDenied면 S3_ACCESS_DENIED 예외를 던진다.") {
                val e = S3Exception.builder()
                    .message("no permission")
                    .statusCode(403)
                    .awsErrorDetails(AwsErrorDetails.builder().errorCode("AccessDenied").build())
                    .build()
                every { presigner.presignPutObject(any<PutObjectPresignRequest>()) } throws e

                shouldThrow<CoreException> {
                    generator.generate(metadata)
                }.errorCode shouldBe FileErrorCode.S3_ACCESS_DENIED
            }

            test("NoSuchBucket, AccessDenied가 아니라면 S3_SERVER_ERROR 예외를 던진다.") {
                val e = S3Exception.builder()
                    .message("boom")
                    .statusCode(500)
                    .awsErrorDetails(AwsErrorDetails.builder().errorCode("BoomError").build())
                    .build()
                every { presigner.presignPutObject(any<PutObjectPresignRequest>()) } throws e

                shouldThrow<CoreException> {
                    generator.generate(metadata)
                }.errorCode shouldBe FileErrorCode.S3_SERVER_ERROR
            }
        }

        test("Exception 발생 시 로깅 후 FAIL_GENERATE_PRESIGNED_URL 예외를 던진다.") {
            every { presigner.presignPutObject(any<PutObjectPresignRequest>()) } throws RuntimeException("unexpected")

            shouldThrow<CoreException> {
                generator.generate(metadata)
            }.errorCode shouldBe FileErrorCode.FAIL_GENERATE_PRESIGNED_URL
        }
    },
)
