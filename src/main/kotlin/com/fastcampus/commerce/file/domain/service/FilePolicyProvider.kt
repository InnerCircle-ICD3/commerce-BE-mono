package com.fastcampus.commerce.file.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.file.domain.error.FileErrorCode
import com.fastcampus.commerce.file.domain.model.DomainType
import com.fastcampus.commerce.file.domain.model.FilePolicy
import com.fastcampus.commerce.file.domain.model.FileType
import com.fastcampus.commerce.file.infrastructure.config.FilePolicyProperties
import org.springframework.stereotype.Component

@Component
class FilePolicyProvider(
    props: FilePolicyProperties,
) {
    private val defaultMap: Map<FileType, FilePolicy> =
        props.default.mapValues { it.value.toPolicy() }

    private val overrideMap: Map<Triple<DomainType, String, FileType>, FilePolicy> =
        props.override.flatMap { override ->
            override.fileTypes.map { (fileType, policy) ->
                Triple(override.domain, override.context, fileType) to policy.toPolicy()
            }
        }.toMap()

    fun resolve(domain: DomainType, context: String, fileType: FileType): FilePolicy {
        return overrideMap[Triple(domain, context, fileType)]
            ?: defaultMap[fileType]
            ?: throw CoreException(FileErrorCode.INVALID_FILE_TYPE)
    }

    private fun FilePolicyProperties.Policy.toPolicy(): FilePolicy {
        return FilePolicy(maxSizeBytes, allowedExtensions)
    }
}
