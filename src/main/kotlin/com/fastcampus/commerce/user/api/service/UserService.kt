package com.fastcampus.commerce.user.api.service

import com.fastcampus.commerce.auth.api.dto.LoginRequest
import com.fastcampus.commerce.auth.api.dto.RegisterRequest
import com.fastcampus.commerce.auth.interfaces.web.security.oauth.NaverUserResponse
import com.fastcampus.commerce.common.error.AuthErrorCode
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.user.api.dto.UserDto
import com.fastcampus.commerce.user.domain.entity.User
import com.fastcampus.commerce.user.domain.enums.UserRole
import com.fastcampus.commerce.user.domain.repository.UserRepository
import com.fastcampus.commerce.user.domain.repository.UserRoleConnectionRepository
import com.fastcampus.commerce.user.domain.repository.UserRoleRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userRoleConnectionRepository: UserRoleConnectionRepository,
    private val userRoleRepository: UserRoleRepository
) {

    fun registerUser(request: RegisterRequest): UserDto {
        return UserDto(
            id = "",
            externalId = "",
            name = "",
            email = request.email,
            nickname = "User",
            profileImage = "",
            roles = emptyList(),
        )
    }

    fun loginUser(request: LoginRequest): UserDto {
        return UserDto(
            id = "",
            externalId = "",
            name = "",
            email = request.email,
            nickname = "User",
            profileImage = "",
            roles = emptyList(),
        )
    }

    fun deleteUser(userId: Long) {
        // TODO: delete 처리
    }

    fun saveOrUpdateUser(naverUser: NaverUserResponse): UserDto {
        return UserDto(
            id = "",
            externalId = "",
            name = "",
            email = naverUser.email,
            nickname = "User",
            profileImage = "",
            roles = emptyList(),
        )
    }

    fun findById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { throw CoreException(AuthErrorCode.USER_NOT_FOUND) }
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun hasRole(userId: Long, requiredRoles: Array<UserRole>): Boolean {
        val userRoleConnections = userRoleConnectionRepository.findAllByUserId(userId)
        val userRoleIds = userRoleConnections.map { it.roleId }
        val userRoles = userRoleRepository.findAllById(userRoleIds)
        val userRoleSet = userRoles.mapNotNull { role ->
            try {
                UserRole.valueOf(role.code)
            } catch (e: IllegalArgumentException) {
                null
            }
        }.toSet()
        return requiredRoles.any { it in userRoleSet }
    }
}
