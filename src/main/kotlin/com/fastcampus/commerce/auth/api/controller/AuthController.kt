package com.fastcampus.commerce.auth.api.controller

import com.fastcampus.commerce.auth.api.dto.AuthResponse
import com.fastcampus.commerce.auth.api.dto.LoginRequest
import com.fastcampus.commerce.auth.api.dto.RegisterRequest
import com.fastcampus.commerce.auth.api.dto.ReissueResponse
import com.fastcampus.commerce.auth.api.service.AuthService
import com.fastcampus.commerce.common.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    fun reissueToken(@RequestParam("refreshToken") refreshToken: String): ApiResponse<ReissueResponse> {
        return ApiResponse.success(authService.reissueToken(refreshToken))
    }

    /**
     * 회원 가입
     */
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ApiResponse<AuthResponse> {
        return ApiResponse.success(authService.register(request))
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ApiResponse<AuthResponse> {
        return ApiResponse.success(authService.login(request))
    }

    /**
     * 로그아웃 - 클라이언트에서 토큰을 삭제하는 방식으로 구현
     * 서버에서는 별도의 처리가 필요 없음
     */
    @PostMapping("/logout")
    fun logout(): ApiResponse<Unit> {
        return ApiResponse.success(Unit)
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/account")
    fun deleteAccount(@AuthenticationPrincipal principal: OAuth2User): ApiResponse<Unit> {
        val userId = principal.name.toLong()
        authService.deleteAccount(userId)
        return ApiResponse.success(Unit)
    }
}
