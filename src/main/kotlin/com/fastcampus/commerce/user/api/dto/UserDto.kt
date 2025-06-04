package com.fastcampus.commerce.user.api.dto

/**
 * User data transfer object
 */
data class UserDto(
    val id: String? = null,
    val externalId: String? = null,
    val name: String? = null,
    val email: String?,
    val nickname: String?,
    val profileImage: String? = null,
    val roles: List<String> = emptyList(),
)
