package com.fastcampus.commerce.common.config

import com.fastcampus.commerce.common.id.IdGenerator
import com.fastcampus.commerce.common.id.SnowflakeIdGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SnowflakeIdConfig {
    @Bean
    fun idGenerator(): IdGenerator {
        val machineId = 1L
        return SnowflakeIdGenerator(machineId)
    }
}
