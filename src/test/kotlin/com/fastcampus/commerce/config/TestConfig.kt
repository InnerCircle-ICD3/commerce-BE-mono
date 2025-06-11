package com.fastcampus.commerce.config

import com.fastcampus.commerce.common.resolver.PageableProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

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
}
