package com.fastcampus.commerce.user.api.dto

import com.fastcampus.commerce.user.domain.enums.UserRole

/**
 * User data transfer object
 */
data class UserDto(
    val id: Long,
    val externalId: String,
    val name: String,
    val email: String,
    val nickname: String,
    val profileImage: String,
    val roles: List<UserRole> = emptyList(),
)
