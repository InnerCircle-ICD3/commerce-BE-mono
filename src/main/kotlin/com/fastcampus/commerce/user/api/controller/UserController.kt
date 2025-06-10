package com.fastcampus.commerce.user.api.controller

import com.fastcampus.commerce.auth.api.dto.LoginRequest
import com.fastcampus.commerce.auth.api.dto.RegisterRequest
import com.fastcampus.commerce.auth.interfaces.web.security.oauth.NaverUserResponse
import com.fastcampus.commerce.user.api.dto.UserDto
import com.fastcampus.commerce.user.api.service.UserService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
) {

    /**
     * Registers a new user.
     *
     * @param registerRequest the registration request containing user details
     * @return the registered user data
     */
    @PostMapping("/register")
    @ResponseBody
    fun registerUser(@RequestBody registerRequest: RegisterRequest): UserDto {
        return userService.registerUser(registerRequest)
    }

    /**
     * Logs in a user.
     *
     * @param loginRequest the login request containing credentials
     * @return the logged-in user data
     */
    @PostMapping("/login")
    @ResponseBody
    fun loginUser(@RequestBody loginRequest: LoginRequest): UserDto {
        return userService.loginUser(loginRequest)
    }

    /**
     * Deletes a user by user ID.
     *
     * @param userId the ID of the user to delete
     */
    @DeleteMapping("/delete/{userId}")
    fun deleteUser(@PathVariable userId: Long) {
        userService.deleteUser(userId)
    }

    /**
     * Saves or updates a user using Naver user response data.
     *
     * @param naverUserResponse the Naver user response data
     * @return the saved or updated user data
     */
    @PutMapping("/naver")
    @ResponseBody
    fun saveOrUpdateUser(@RequestBody naverUserResponse: NaverUserResponse): UserDto {
        return userService.saveOrUpdateUser(naverUserResponse)
    }

}
