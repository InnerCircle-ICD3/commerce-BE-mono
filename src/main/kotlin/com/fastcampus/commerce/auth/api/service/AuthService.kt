package com.fastcampus.commerce.auth.api.service

import com.fastcampus.commerce.auth.TokenProvider
import com.fastcampus.commerce.auth.api.dto.AuthResponse
import com.fastcampus.commerce.auth.api.dto.LoginRequest
import com.fastcampus.commerce.auth.api.dto.RegisterRequest
import com.fastcampus.commerce.auth.api.dto.ReissueResponse
import com.fastcampus.commerce.common.error.AuthErrorCode
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.user.api.controller.UserController
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val tokenProvider: TokenProvider,
    private val userController: UserController,
) {
    /**
     * 액세스 토큰을 재발급합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로 발급된 액세스 토큰
     * @throws CoreException 토큰이 유효하지 않거나 만료된 경우
     */
    fun reissueToken(refreshToken: String): ReissueResponse {
        try {
            val accessToken = tokenProvider.refreshAccessToken(refreshToken)
            return ReissueResponse(accessToken = accessToken)
        } catch (e: Exception) {
            throw CoreException(AuthErrorCode.INVALID_TOKEN)
        }
    }

    /**
     * 사용자 등록을 처리합니다.
     *
     * @param registerRequest 사용자 등록 요청 정보
     * @return 인증 응답 (토큰 및 사용자 정보)
     */
    fun register(registerRequest: RegisterRequest): AuthResponse {
        // 사용자 등록 API 호출
        val userDto = userController.registerUser(registerRequest)

        // 토큰 생성
        val accessToken = tokenProvider.createAccessToken(userDto.id)
        val refreshToken = tokenProvider.createRefreshToken(userDto.id)

        // 인증 응답 반환
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userDto.id,
            email = userDto.email,
            nickname = userDto.nickname,
        )
    }

    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param loginRequest 로그인 요청 정보
     * @return 인증 응답 (토큰 및 사용자 정보)
     */
    fun login(loginRequest: LoginRequest): AuthResponse {
        // 로그인 API 호출
        val userDto = userController.loginUser(loginRequest)

        // 토큰 생성
        val accessToken = tokenProvider.createAccessToken(userDto.id)
        val refreshToken = tokenProvider.createRefreshToken(userDto.id)

        // 인증 응답 반환
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userDto.id,
            email = userDto.email,
            nickname = userDto.nickname,
        )
    }

    /**
     * 사용자 계정을 삭제합니다.
     *
     * @param userId 삭제할 사용자 ID
     */
    fun deleteAccount(userId: Long) {
        userController.deleteUser(userId)
    }
}
