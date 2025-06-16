package com.fastcampus.commerce.config

import com.fastcampus.commerce.auth.interfaces.web.security.resolver.RoleBasedUserArgumentResolver
import com.fastcampus.commerce.common.resolver.PageableProperties
import com.fastcampus.commerce.user.api.service.UserService
import com.fastcampus.commerce.user.domain.entity.User
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.core.MethodParameter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer
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

    @Primary
    @Bean
    fun mockUserService(): UserService {
        return mockk<UserService>()
    }

    @Primary
    @Bean
    fun testRoleBasedUserArgumentResolver(userService: UserService): RoleBasedUserArgumentResolver {
        return object : RoleBasedUserArgumentResolver(userService) {
            override fun resolveArgument(
                parameter: MethodParameter,
                mavContainer: ModelAndViewContainer?,
                webRequest: NativeWebRequest,
                binderFactory: WebDataBinderFactory?
            ): Any? {
                return mockk<User> {
                    every { id } returns 1L
                    every { name } returns "testUser"
                }
            }
        }
    }
}
