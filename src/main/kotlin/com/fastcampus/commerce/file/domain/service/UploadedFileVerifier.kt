package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.file.domain.model.UploadUrl
import com.fastcampus.commerce.file.infrastructure.s3.S3FileReader
import org.springframework.stereotype.Component

@Component
class UploadedFileVerifier(
    private val fileReader: FileReader,
    private val s3FileReader: S3FileReader,
) {
    fun verifyFileWithS3Urls(fileUrls: List<String>) {
        fileUrls.forEach { verifyFileFromUploadedUrl(UploadUrl(it)) }
    }

    private fun verifyFileFromUploadedUrl(uploadUrl: UploadUrl) {
        val s3Key = uploadUrl.storedPath
        val metadata = fileReader.getFileMetadataByStoredPath(s3Key)
        val actualFile = s3FileReader.read(s3Key)

        actualFile.verify(metadata)
    }
}
