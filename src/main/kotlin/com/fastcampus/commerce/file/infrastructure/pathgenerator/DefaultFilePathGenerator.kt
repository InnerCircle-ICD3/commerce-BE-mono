package com.fastcampus.commerce.file.infrastructure.pathgenerator

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.application.request.GeneratePresignedUrlRequest
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.model.FilePath
import com.fastcampus.commerce.file.domain.repository.UniqueIdGenerator
import com.fastcampus.commerce.file.domain.service.FilePathGenerator
import org.springframework.stereotype.Component

@Component
class DefaultFilePathGenerator(
    private val uniqueIdGenerator: UniqueIdGenerator,
) : FilePathGenerator {
    override fun generate(contextKeyId: String, request: GeneratePresignedUrlRequest): FilePath {
        // TODO: 추후 Request가 아니라 객체를 통해서 필요한 것만 받자 (e.g. FileName(name = "coffee", extension = "png"))
        if (!request.fileName.contains('.')) {
            throw CoreException(FileErrorCode.INVALID_FILE_NAME)
        }

        val extension = request.fileName.substringAfterLast('.', "").lowercase()
        val fileName = "${request.domainContext}-${uniqueIdGenerator.generate()}.$extension"
        val path = "${request.domainType.domainName}/$contextKeyId/$fileName"
        return FilePath(path = path, fileName = fileName)
    }
}
