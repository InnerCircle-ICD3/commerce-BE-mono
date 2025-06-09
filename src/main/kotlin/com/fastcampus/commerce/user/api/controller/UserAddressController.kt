package com.fastcampus.commerce.user.api.controller

import com.fastcampus.commerce.user.api.controller.request.RegisterUserAddressApiRequest
import com.fastcampus.commerce.user.api.controller.request.UpdateUserAddressApiRequest
import com.fastcampus.commerce.user.api.controller.response.DeleteUserAddressApiResponse
import com.fastcampus.commerce.user.api.controller.response.RegisterUserAddressApiResponse
import com.fastcampus.commerce.user.api.controller.response.UpdateUserAddressApiResponse
import com.fastcampus.commerce.user.api.controller.response.UserAddressApiResponse
import com.fastcampus.commerce.user.api.service.UserAddressService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/users/addresses")
@RestController
class UserAddressController(
    private val userAddressService: UserAddressService,
) {
    @GetMapping
    fun getUserAddresses(): List<UserAddressApiResponse> {
        val userId = 1L
        return userAddressService.getUserAddresses(userId)
            .map(UserAddressApiResponse::from)
    }

    @PostMapping
    fun registerUserAddress(
        @RequestBody request: RegisterUserAddressApiRequest,
    ): RegisterUserAddressApiResponse {
        val userId = 1L
        val userAddressId = userAddressService.register(userId, request.toServiceRequest())
        return RegisterUserAddressApiResponse(userAddressId)
    }

    @PutMapping("/{userAddressId}")
    fun updateUserAddress(
        @PathVariable userAddressId: Long,
        @RequestBody request: UpdateUserAddressApiRequest,
    ): UpdateUserAddressApiResponse {
        val userId = 1L
        userAddressService.update(userId, userAddressId, request.toServiceRequest())
        return UpdateUserAddressApiResponse(userAddressId)
    }

    @DeleteMapping("/{userAddressId}")
    fun deleteUserAddress(
        @PathVariable userAddressId: Long,
    ): DeleteUserAddressApiResponse {
        val userId = 1L
        userAddressService.delete(userId, userAddressId)
        return DeleteUserAddressApiResponse()
    }
}
