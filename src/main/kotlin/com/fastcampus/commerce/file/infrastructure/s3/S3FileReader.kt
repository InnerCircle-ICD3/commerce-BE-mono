package com.fastcampus.commerce.file.infrastructure.s3

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.model.ActualFile
import com.fastcampus.commerce.file.domain.service.UploadFileReader
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception

@Component
class S3FileReader(
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String,
    private val s3Client: S3Client,
) : UploadFileReader {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun read(key: String): ActualFile {
        try {
            val headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()

            val headObjectResponse = s3Client.headObject(headObjectRequest)

            val actualSize = headObjectResponse.contentLength()
            val actualContentType = headObjectResponse.contentType()
            log.debug("actualSize: $actualSize, actualContentType: $actualContentType")

            return ActualFile(
                size = actualSize,
                contentType = actualContentType,
            )
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
