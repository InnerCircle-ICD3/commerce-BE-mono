package com.fastcampus.commerce.user.api.service

import com.fastcampus.commerce.common.error.AuthErrorCode
import com.fastcampus.commerce.common.error.CoreException
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
