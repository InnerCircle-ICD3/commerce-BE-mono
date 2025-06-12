package com.fastcampus.commerce.user.api.service

import com.fastcampus.commerce.auth.api.dto.LoginRequest
import com.fastcampus.commerce.auth.api.dto.RegisterRequest
import com.fastcampus.commerce.auth.interfaces.web.security.oauth.NaverUserResponse
import com.fastcampus.commerce.common.error.AuthErrorCode
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.user.api.dto.UserDto
import com.fastcampus.commerce.user.domain.entity.User
import com.fastcampus.commerce.user.domain.entity.UserRoleConnection
import com.fastcampus.commerce.user.domain.enums.UserRole
import com.fastcampus.commerce.user.domain.repository.UserRepository
import com.fastcampus.commerce.user.domain.repository.UserRoleConnectionRepository
import com.fastcampus.commerce.user.domain.repository.UserRoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userRoleConnectionRepository: UserRoleConnectionRepository,
    private val userRoleRepository: UserRoleRepository
) {

    @Transactional(rollbackFor = [Exception::class])
    fun registerUser(request: RegisterRequest): UserDto {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.email)) {
            throw CoreException(AuthErrorCode.EMAIL_ALREADY_EXISTS)
        }

        // 2. User 엔티티 생성
        val user = User(
            externalId = "",   // TODO: UUID 구현체 구현 예정
            name = request.name,
            email = request.email,
            nickname = request.nickname ?: request.email.substringBefore("@"),
            profileImage = request.profileImage,
            // 기타 필요한 필드 추가
        )

        // 3. User 저장
        val savedUser = userRepository.save(user)

        // 4. 기본 역할 할당 (ex. USER)
        val userRole = userRoleRepository.findByCode(UserRole.USER.name)
            ?: throw CoreException(AuthErrorCode.ROLE_NOT_FOUND)
        val userRoleConnection = UserRoleConnection(
            userId = savedUser.id!!,
            roleId = userRole.id!!,
        )
        userRoleConnectionRepository.save(userRoleConnection)

        // 5. DTO 변환
        return UserDto(
            id = savedUser.id.toString(),
            externalId = savedUser.externalId ?: "",
            name = savedUser.name,
            email = savedUser.email,
            nickname = savedUser.nickname,
            profileImage = savedUser.profileImage ?: "",
            roles = listOf(UserRole.USER)
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
