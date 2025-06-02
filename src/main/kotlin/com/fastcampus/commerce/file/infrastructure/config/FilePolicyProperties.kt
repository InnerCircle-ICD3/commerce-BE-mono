package com.fastcampus.commerce.file.infrastructure.config

import com.fastcampus.commerce.file.domain.model.DomainType
import com.fastcampus.commerce.file.domain.model.FileType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

const val ONE_MB = 1024 * 1024

@ConfigurationProperties(prefix = "file.policy")
@Component
class FilePolicyProperties {
    lateinit var default: Map<FileType, Policy>
    var override: List<OverridePolicy> = emptyList()

    data class Policy(
        val maxSizeMb: Int,
        val allowedExtensions: Set<String>,
    ) {
        val maxSizeBytes: Int = maxSizeMb * ONE_MB
    }

    data class OverridePolicy(
        val domain: DomainType,
        val context: String,
        val fileTypes: Map<FileType, Policy>,
    )
}
