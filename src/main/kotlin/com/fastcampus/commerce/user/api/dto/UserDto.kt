package com.fastcampus.commerce.user.api.dto

/**
 * User data transfer object
 */
data class UserDto(
    val id: String,
    val externalId: String,
    val name: String,
    val email: String,
    val nickname: String,
    val profileImage: String,
    val roles: List<String> = emptyList(),
)
