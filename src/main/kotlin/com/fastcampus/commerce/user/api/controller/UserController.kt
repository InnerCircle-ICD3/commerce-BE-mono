package com.fastcampus.commerce.user.api.controller

import com.fastcampus.commerce.auth.api.dto.LoginRequest
import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.user.api.controller.request.MyInfoResponse
import com.fastcampus.commerce.user.api.controller.request.UpdateMyInfoRequest
import com.fastcampus.commerce.user.api.dto.UserDto
import com.fastcampus.commerce.user.api.service.UserService
import com.fastcampus.commerce.user.domain.enums.UserRole
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
) {
    /**
     * Logs in a user.
     *
     * @param loginRequest the login request containing credentials
     * @return the logged-in user data
     */
    @PostMapping("/login")
    @ResponseBody
    fun loginUser(
        @RequestBody loginRequest: LoginRequest,
    ): UserDto {
        print("${loginRequest.authInfo.provider} ${loginRequest.authInfo.token}")
        print("${loginRequest.userProfile.email} ${loginRequest.userProfile.name}")
        print("${loginRequest.userProfile.nickname} ${loginRequest.userProfile.profileImage}")
        return userService.loginUser(loginRequest)
    }

    @GetMapping("/me")
    fun me(
        @WithRoles([UserRole.USER]) user: LoginUser,
    ): MyInfoResponse {
        return userService.getMyInfo(user.id)
    }

    @PatchMapping("/me")
    fun updateMyInfo(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @Valid @RequestBody request: UpdateMyInfoRequest,
    ) {
        userService.updateMyInfo(user.id, request)
    }

    /**
     * Deletes a user by user ID.
     *
     * @param userId the ID of the user to delete
     */
    @DeleteMapping("/delete")
    fun deleteUser(
        @RequestBody userId: Long,
    ) {
        userService.deleteUser(userId)
    }
}
