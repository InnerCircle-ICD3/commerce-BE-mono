package com.fastcampus.commerce.auth.interfaces.web.security.resolver

import com.fastcampus.commerce.config.TestConfig
import com.fastcampus.commerce.user.api.service.UserService
import com.fastcampus.commerce.user.domain.entity.User
import com.fastcampus.commerce.user.domain.enums.UserRole
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [SampleController::class])
@Import(RoleBasedUserArgumentResolver::class, TestConfig::class)
class RoleBasedUserArgumentResolverTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userService: UserService

    @Test
    fun `권한이 맞을 때는 User가 주입된다`() {
        // given
        val userId = 1L
        val user = User(
            externalId = "123",
            name = "test",
            email = "test@naver.com",
            nickname = "testNickName"
        )
        every { userService.findById(userId) } returns user
        every { userService.hasRole(userId, arrayOf(UserRole.ADMIN)) } returns true

        // SecurityContext 세팅 (Spring Security Mock)
        val authentication = UsernamePasswordAuthenticationToken(userId.toString(), null, listOf())
        SecurityContextHolder.getContext().authentication = authentication

        // when & then
        mockMvc.perform(get("/api/test/with-roles"))
            .andExpect(status().isOk)
        // 기타 검증
    }
}
