package com.fastcampus.commerce.user.api.dto

/**
 * User data transfer object
 */
data class UserDto(
    val id: Long,
    val email: String?,
    val nickname: String,
    val roles: List<String> = emptyList()
)
