package com.fastcampus.commerce.user.api.controller

import com.fastcampus.commerce.auth.api.dto.LoginRequest
import com.fastcampus.commerce.auth.api.dto.RegisterRequest
import com.fastcampus.commerce.auth.interfaces.web.security.oauth.NaverUserResponse
import com.fastcampus.commerce.user.api.dto.UserDto
import com.fastcampus.commerce.user.api.service.UserService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
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
}
