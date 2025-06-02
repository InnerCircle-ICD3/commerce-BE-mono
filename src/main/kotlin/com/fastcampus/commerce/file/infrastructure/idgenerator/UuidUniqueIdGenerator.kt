package com.fastcampus.commerce.file.infrastructure.idgenerator

import com.fastcampus.commerce.file.domain.repository.UniqueIdGenerator
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UuidUniqueIdGenerator : UniqueIdGenerator {
    override fun generate(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }
}
