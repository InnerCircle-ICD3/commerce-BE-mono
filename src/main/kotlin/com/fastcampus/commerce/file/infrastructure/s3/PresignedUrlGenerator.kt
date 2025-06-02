package com.fastcampus.commerce.file.infrastructure.s3

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.entity.FileMetadata
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.service.UploadUrlGenerator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration

@Component
class PresignedUrlGenerator(
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String,
    @Value("\${cloud.aws.s3.presign-ttl-ms}") private val ttlMs: Long,
    private val presigner: S3Presigner,
) : UploadUrlGenerator {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun generate(fileMetadata: FileMetadata): String {
        try {
            log.info("Presigned URL 생성 요청: ${fileMetadata.storedPath}")

            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileMetadata.storedPath)
                .contentType(fileMetadata.contentType)
                .contentLength(fileMetadata.fileSize.toLong())
                .build()

            val presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMillis(ttlMs))
                .putObjectRequest(putObjectRequest)
                .build()

            val response = presigner.presignPutObject(presignRequest)
            return response.url().toString()
        } catch (e: SdkClientException) {
            log.error("S3 Client Error: ${e.message}", e)
            throw CoreException(FileErrorCode.S3_CLIENT_ERROR)
        } catch (e: S3Exception) {
            log.error("S3 Server Error: ${e.message}", e)
            val errorCode = e.awsErrorDetails().errorCode()

            when (errorCode) {
                "NoSuchBucket" -> throw CoreException(FileErrorCode.S3_BUCKET_NOT_FOUND)
                "AccessDenied" -> throw CoreException(FileErrorCode.S3_ACCESS_DENIED)
                else -> throw CoreException(FileErrorCode.S3_SERVER_ERROR)
            }
        } catch (e: Exception) {
            log.error("S3 Error: ${e.message}", e)
            throw CoreException(FileErrorCode.FAIL_GENERATE_PRESIGNED_URL)
        }
    }
}
