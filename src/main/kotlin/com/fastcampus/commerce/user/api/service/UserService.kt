package com.fastcampus.commerce.user.api.service

import com.fastcampus.commerce.auth.api.dto.LoginRequest
import com.fastcampus.commerce.common.error.AuthErrorCode
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.id.IdGenerator
import com.fastcampus.commerce.user.api.dto.UserDto
import com.fastcampus.commerce.user.domain.entity.Oauth2Provider
import com.fastcampus.commerce.user.domain.entity.User
import com.fastcampus.commerce.user.domain.entity.UserOauth2Connection
import com.fastcampus.commerce.user.domain.entity.UserRoleConnection
import com.fastcampus.commerce.user.domain.enums.UserRole
import com.fastcampus.commerce.user.domain.repository.Oauth2ProviderRepository
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
    private val oauth2ProviderRepository: Oauth2ProviderRepository,
    private val userOauth2ConnectionRepository: UserOauth2ConnectionRepository,
    private val idGenerator: IdGenerator,
) {
    @Transactional(rollbackFor = [Exception::class])
    fun loginUser(request: LoginRequest): UserDto {
        // 1. 입력값 필수 체크 (email, name 등)
        val provider = request.authInfo.provider
        val email = request.userProfile.email
            ?: throw CoreException(AuthErrorCode.INVALID_USER_PROFILE)
        val name = request.userProfile.name ?: ""
        val nickname = request.userProfile.nickname ?: ""
        val profileImage = request.userProfile.profileImage ?: ""

        // [NOTE] 현재는 providerId 없이 이메일로만 유저를 식별합니다.
        val existingUser = userRepository.findByEmail(email)
        // 기존 유저 return
        if (existingUser != null) {
            // roles 가져오기
            val roleConnections = userRoleConnectionRepository.findAllByUserId(existingUser.id!!)
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
            externalId = idGenerator.generate().toString(),
            name = name,
            email = email,
            nickname = nickname,
            profileImage = profileImage,
        )
        val savedUser = userRepository.save(newUser)

        // 3. UserRoleConnection 저장
        /*val defaultRole = userRoleRepository.findByCode(UserRole.USER.name)
            ?: throw CoreException(AuthErrorCode.INVALID_USER_PROFILE, "기본 권한이 없습니다.")
        val userRoleConnection = UserRoleConnection(
            userId = savedUser.id!!,
            roleId = defaultRole.id!!
        )*/
        val userRoleConnection = UserRoleConnection(
            userId = savedUser.id!!,
            roleId = UserRole.USER.ordinal.toLong(), // 또는 상수값 직접 입력
        )
        userRoleConnectionRepository.save(userRoleConnection)

        // 4. Oauth2Provider 저장 or 조회 (provider가 "naver"라면, 없으면 신규 등록)
        val oauth2Provider = oauth2ProviderRepository.findByName(provider)
            ?: oauth2ProviderRepository.save(
                Oauth2Provider(
                    name = provider,
                    isActive = true,
                ),
            )

        // 5. UserOauth2Connection 저장 (여기서는 email을 oauth2Id로 사용, 향후 네이버 id로 확장 가능)
        val userOauth2Connection = UserOauth2Connection(
            userId = savedUser.id!!,
            providerId = oauth2Provider.id!!,
            oauth2Id = email, // 지금은 email, 추후 네이버/카카오 id로 확장 가능
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
