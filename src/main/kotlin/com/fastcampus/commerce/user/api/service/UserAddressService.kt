package com.fastcampus.commerce.user.api.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.user.api.service.request.RegisterUserAddressRequest
import com.fastcampus.commerce.user.api.service.request.UpdateUserAddressRequest
import com.fastcampus.commerce.user.api.service.response.UserAddressResponse
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
    @Transactional(readOnly = true)
    fun getUserAddresses(userId: Long): List<UserAddressResponse> {
        val addresses = userAddressRepository.getAllByUserId(userId)
        return addresses.map(UserAddressResponse::from)
    }

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

    @Transactional
    fun update(userId: Long, userAddressId: Long, request: UpdateUserAddressRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { throw CoreException(UserErrorCode.USER_NOT_FOUND) }

        val userAddress = userAddressRepository.findById(userAddressId)
            .orElseThrow { throw CoreException(UserErrorCode.USER_ADDRESS_NOT_FOUND) }
        if (userAddress.userId != userId) {
            throw CoreException(UserErrorCode.UNAUTHORIZED_USER_ADDRESS_UPDATE)
        }
        if (request.isDefault) {
            val defaultAddress = userAddressRepository.findDefaultByUserId(userId)
            defaultAddress.ifPresent { address -> address.unsetAsDefault() }
        }
        userAddress.update(request.toUpdater())
    }

    @Transactional
    fun delete(userId: Long, userAddressId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { throw CoreException(UserErrorCode.USER_NOT_FOUND) }

        val userAddress = userAddressRepository.findById(userAddressId)
            .orElseThrow { throw CoreException(UserErrorCode.USER_ADDRESS_NOT_FOUND) }
        if (userAddress.userId != userId) {
            throw CoreException(UserErrorCode.UNAUTHORIZED_USER_ADDRESS_DELETE)
        }
        userAddressRepository.delete(userAddress)
    }
}
