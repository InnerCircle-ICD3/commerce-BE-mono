package com.fastcampus.commerce.config

import com.fastcampus.commerce.common.resolver.PageableProperties
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import java.time.format.DateTimeFormatter

@TestConfiguration
@EnableWebSecurity
class TestConfig {
    @Bean
    fun testSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
    }

    @Bean
    fun pageableProperties(): PageableProperties {
        return PageableProperties(
            oneIndexedParameters = true,
            defaultPageSize = 10,
            maxPageSize = 50,
        )
    }

    @Bean
    fun jackson2ObjectMapperBuilder(): Jackson2ObjectMapperBuilder {
        return Jackson2ObjectMapperBuilder()
            .serializers(LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
    }
}
