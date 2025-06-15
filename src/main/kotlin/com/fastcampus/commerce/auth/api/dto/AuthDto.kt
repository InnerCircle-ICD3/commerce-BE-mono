package com.fastcampus.commerce.auth.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Authentication-related DTOs
 */

/**
 * User registration request DTO
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val name: String,
    val profileImage: String,
)

/**
 * Login request DTO
 */
data class LoginRequest(
    @JsonProperty("auth_info")
    val authInfo: AuthInfo,
    @JsonProperty("user_profile")
    val userProfile: UserProfile,
)

data class AuthInfo(
    val provider: String,
    val token: String, // 프론트에서 받은 소셜 accessToken
)

data class UserProfile(
    val email: String?,
    val name: String?,
    val nickname: String?,
    @JsonProperty("profile_image")
    val profileImage: String?,
    val gender: String?,
    val birthday: String?,
    val age: String?
)

/**
 * Authentication response DTO
 */
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String?,
    val nickname: String,
)

data class ReissueResponse(
    val accessToken: String,
)
