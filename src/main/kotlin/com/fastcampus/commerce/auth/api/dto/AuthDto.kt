package com.fastcampus.commerce.auth.api.dto

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
    val name: String? = null,
)

/**
 * Login request DTO
 */
data class LoginRequest(
    val email: String,
    val password: String,
)

/**
 * Authentication response DTO
 */
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val email: String?,
    val nickname: String,
)

data class ReissueResponse(
    val accessToken: String,
)
