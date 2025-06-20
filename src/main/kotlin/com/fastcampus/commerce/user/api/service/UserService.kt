package com.fastcampus.commerce.user.api.service

import com.fastcampus.commerce.auth.api.dto.LoginRequest
import com.fastcampus.commerce.common.error.AuthErrorCode
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.id.UniqueIdGenerator
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.user.api.controller.request.MyInfoResponse
import com.fastcampus.commerce.user.api.controller.request.UpdateMyInfoRequest
import com.fastcampus.commerce.user.api.dto.UserDto
import com.fastcampus.commerce.user.domain.entity.User
import com.fastcampus.commerce.user.domain.entity.UserOauth2Connection
import com.fastcampus.commerce.user.domain.entity.UserRoleConnection
import com.fastcampus.commerce.user.domain.enums.UserRole
import com.fastcampus.commerce.user.domain.error.UserErrorCode
import com.fastcampus.commerce.user.domain.repository.UserOauth2ConnectionRepository
import com.fastcampus.commerce.user.domain.repository.UserRepository
import com.fastcampus.commerce.user.domain.repository.UserRoleConnectionRepository
import com.fastcampus.commerce.user.domain.repository.UserRoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userRoleConnectionRepository: UserRoleConnectionRepository,
    private val userRoleRepository: UserRoleRepository,
    private val userOauth2ConnectionRepository: UserOauth2ConnectionRepository,
    private val timeProvider: TimeProvider,
) {
    @Transactional(rollbackFor = [Exception::class])
    fun loginUser(request: LoginRequest): UserDto {
        val token = request.authInfo.token
        val connection = userOauth2ConnectionRepository.findByProviderIdAndOauth2Id(1L, token)
        if (connection.isPresent) {
            val userId = connection.get().userId
            val existingUser = userRepository.findById(userId)
                .orElseThrow { throw CoreException(AuthErrorCode.USER_NOT_FOUND) }
            val roleConnections = userRoleConnectionRepository.findAllByUserId(userId)
            val roleIds = roleConnections.map { it.roleId }
            val userRoles = userRoleRepository.findAllById(roleIds)
                .mapNotNull { UserRole.values().find { ur -> ur.name == it.code } }
            return UserDto(
                id = existingUser.id!!,
                externalId = existingUser.externalId,
                name = existingUser.name,
                email = existingUser.email,
                nickname = existingUser.nickname,
                profileImage = existingUser.profileImage!!,
                roles = userRoles,
            )
        }

        // 2. User 저장 (snowflake externalId는 별도 생성 함수로)
        val newUser = User(
            externalId = UniqueIdGenerator.generateUserId(timeProvider.now().toLocalDate()),
            name = request.userProfile.name ?: "",
            email = request.userProfile.email ?: "",
            nickname = request.userProfile.nickname ?: "",
            profileImage = request.userProfile.profileImage,
        )
        val savedUser = userRepository.save(newUser)

        // 3. UserRoleConnection 저장
        val userRoleConnection = UserRoleConnection(
            userId = savedUser.id!!,
            roleId = UserRole.USER.roleId,
        )
        userRoleConnectionRepository.save(userRoleConnection)

        val userOauth2Connection = UserOauth2Connection(
            userId = savedUser.id!!,
            providerId = 1,
            oauth2Id = token,
        )
        userOauth2ConnectionRepository.save(userOauth2Connection)

        // 6. DTO 반환
        return UserDto(
            id = savedUser.id!!,
            externalId = savedUser.externalId,
            name = savedUser.name,
            email = savedUser.email,
            nickname = savedUser.nickname,
            profileImage = savedUser.profileImage!!,
            roles = listOf(UserRole.USER),
        )
    }

    @Transactional(rollbackFor = [Exception::class])
    fun deleteUser(userId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { throw CoreException(AuthErrorCode.USER_NOT_FOUND) }

        user.isDeleted = true
        user.deletedAt = java.time.LocalDateTime.now()
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun getUser(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { throw CoreException(AuthErrorCode.USER_NOT_FOUND) }
    }

    @Transactional(readOnly = true)
    fun hasRole(userId: Long, requiredRoles: Array<UserRole>): Boolean {
        val userRoleConnections = userRoleConnectionRepository.findAllByUserId(userId)
        val userRoleIds = userRoleConnections.map { it.roleId }
        val userRoles = userRoleRepository.findAllById(userRoleIds)
        val userRoleSet = userRoles.mapNotNull { UserRole.from(it.code) }.toSet()

        if (userRoleSet.contains(UserRole.SUPER_ADMIN)) {
            return true
        }
        if (userRoleSet.contains(UserRole.ADMIN) &&
            requiredRoles.any { it == UserRole.ADMIN || it == UserRole.USER }
        ) {
            return true
        }
        return requiredRoles.any { it in userRoleSet }
    }

    @Transactional(readOnly = true)
    fun getMyInfo(userId: Long): MyInfoResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { CoreException(UserErrorCode.USER_NOT_FOUND) }
        return MyInfoResponse.of(user)
    }

    @Transactional
    fun updateMyInfo(userId: Long, request: UpdateMyInfoRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { CoreException(UserErrorCode.USER_NOT_FOUND) }
        user.updateNickname(request.nickname)
    }
}
