package com.fastcampus.commerce.auth.api.controller

import com.fastcampus.commerce.auth.api.dto.AuthResponse
import com.fastcampus.commerce.auth.api.dto.LoginRequest
import com.fastcampus.commerce.auth.api.dto.ReissueResponse
import com.fastcampus.commerce.auth.api.service.AuthService
import com.fastcampus.commerce.common.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    fun reissueToken(
        @RequestParam("refreshToken") refreshToken: String,
    ): ApiResponse<ReissueResponse> {
        return ApiResponse.success(authService.reissueToken(refreshToken))
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
    ): ApiResponse<AuthResponse> {
        return ApiResponse.success(authService.login(request))
    }
}
