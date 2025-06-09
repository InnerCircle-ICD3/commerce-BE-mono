package com.fastcampus.commerce.user.api.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.user.api.service.request.RegisterUserAddressRequest
import com.fastcampus.commerce.user.domain.error.UserErrorCode
import com.fastcampus.commerce.user.domain.repository.UserAddressRepository
import com.fastcampus.commerce.user.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAddressService(
    private val userRepository: UserRepository,
    private val userAddressRepository: UserAddressRepository,
) {
    @Transactional
    fun register(userId: Long, request: RegisterUserAddressRequest): Long {
        val user = userRepository.findById(userId)
            .orElseThrow { throw CoreException(UserErrorCode.USER_NOT_FOUND) }

        if (request.isDefault) {
            val defaultAddress = userAddressRepository.findDefaultByUserId(userId)
            defaultAddress.ifPresent { address -> address.unsetAsDefault() }
        }
        return userAddressRepository.save(request.toEntity(userId)).id!!
    }
}
