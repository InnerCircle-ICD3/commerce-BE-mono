package com.fastcampus.commerce.user.api.controller

import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.user.api.controller.request.RegisterUserAddressApiRequest
import com.fastcampus.commerce.user.api.controller.request.UpdateUserAddressApiRequest
import com.fastcampus.commerce.user.api.controller.response.DefaultAddressApiResponse
import com.fastcampus.commerce.user.api.controller.response.DeleteUserAddressApiResponse
import com.fastcampus.commerce.user.api.controller.response.RegisterUserAddressApiResponse
import com.fastcampus.commerce.user.api.controller.response.UpdateUserAddressApiResponse
import com.fastcampus.commerce.user.api.controller.response.UserAddressApiResponse
import com.fastcampus.commerce.user.api.service.UserAddressService
import com.fastcampus.commerce.user.domain.enums.UserRole
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RequestMapping("/users/addresses")
@RestController
class UserAddressController(
    private val userAddressService: UserAddressService,
) {
    @GetMapping
    fun getUserAddresses(
        @WithRoles([UserRole.USER]) user: LoginUser,
    ): List<UserAddressApiResponse> {
        return userAddressService.getUserAddresses(user.id)
            .map(UserAddressApiResponse::from)
    }

    @GetMapping("/{userAddressId}")
    fun getUserAddress(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @PathVariable userAddressId: Long,
    ): UserAddressApiResponse {
        return UserAddressApiResponse.from(userAddressService.getUserAddress(user.id, userAddressId))
    }

    @GetMapping("/default")
    fun getDefaultUserAddress(
        @WithRoles([UserRole.USER]) user: LoginUser,
    ): DefaultAddressApiResponse {
        val response = userAddressService.findDefaultUserAddress(user.id)
        return DefaultAddressApiResponse.from(response)
    }

    @PostMapping
    fun registerUserAddress(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @Valid @RequestBody request: RegisterUserAddressApiRequest,
    ): RegisterUserAddressApiResponse {
        val userAddressId = userAddressService.register(user.id, request.toServiceRequest())
        return RegisterUserAddressApiResponse(userAddressId)
    }

    @PutMapping("/{userAddressId}")
    fun updateUserAddress(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @PathVariable userAddressId: Long,
        @Valid @RequestBody request: UpdateUserAddressApiRequest,
    ): UpdateUserAddressApiResponse {
        userAddressService.update(user.id, userAddressId, request.toServiceRequest())
        return UpdateUserAddressApiResponse(userAddressId)
    }

    @DeleteMapping("/{userAddressId}")
    fun deleteUserAddress(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @PathVariable userAddressId: Long,
    ): DeleteUserAddressApiResponse {
        userAddressService.delete(user.id, userAddressId)
        return DeleteUserAddressApiResponse()
    }
}
