package com.fastcampus.commerce.user.api.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.user.api.service.request.RegisterUserAddressRequest
import com.fastcampus.commerce.user.api.service.request.UpdateUserAddressRequest
import com.fastcampus.commerce.user.domain.entity.User
import com.fastcampus.commerce.user.domain.entity.UserAddress
import com.fastcampus.commerce.user.domain.error.UserErrorCode
import com.fastcampus.commerce.user.domain.repository.UserAddressRepository
import com.fastcampus.commerce.user.domain.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import java.util.Optional

class UserAddressServiceTest : FunSpec({

    val userRepository = mockk<UserRepository>()
    val userAddressRepository = mockk<UserAddressRepository>()
    val userAddressService = UserAddressService(userRepository, userAddressRepository)

    val userId = 1L
    val user = mockk<User>()

    context("getUserAddresses") {
        test("사용자의 배송지 목록을 조회할 수 있다") {
            val address1 = UserAddress(
                userId = userId,
                alias = "집",
                recipientName = "홍길동",
                recipientPhone = "010-1234-5678",
                zipCode = "12345",
                address1 = "서울시 강남구",
                address2 = "테헤란로 123",
                isDefault = true,
            ).apply { id = 100L }

            val address2 = UserAddress(
                userId = userId,
                alias = "회사",
                recipientName = "홍길동",
                recipientPhone = "010-1234-5678",
                zipCode = "54321",
                address1 = "부산시 해운대구",
                isDefault = false,
            ).apply { id = 101L }

            every { userAddressRepository.getAllByUserId(userId) } returns listOf(address1, address2)

            val result = userAddressService.getUserAddresses(userId)

            result shouldHaveSize 2
            result[0].addressId shouldBe 100L
            result[0].alias shouldBe "집"
            result[0].isDefault shouldBe true
            result[1].addressId shouldBe 101L
            result[1].alias shouldBe "회사"
            result[1].isDefault shouldBe false
        }

        test("배송지가 없는 경우 빈 목록을 반환한다") {
            every { userAddressRepository.getAllByUserId(userId) } returns emptyList()

            val result = userAddressService.getUserAddresses(userId)

            result shouldHaveSize 0
        }
    }

    context("getUserAddress") {
        val userAddressId = 400L

        test("특정 사용자 주소를 조회할 수 있다") {
            val userAddress = UserAddress(
                userId = userId,
                alias = "집",
                recipientName = "홍길동",
                recipientPhone = "01012345678",
                zipCode = "12345",
                address1 = "서울시 강남구",
                address2 = "테헤란로 123",
                isDefault = true,
            ).apply { id = userAddressId }

            every { userAddressRepository.findByIdAndUserId(userAddressId, userId) } returns Optional.of(userAddress)

            val result = userAddressService.getUserAddress(userId, userAddressId)

            result.addressId shouldBe userAddressId
            result.alias shouldBe "집"
            result.recipientName shouldBe "홍길동"
            result.recipientPhone shouldBe "010-1234-5678"
            result.isDefault shouldBe true
        }

        test("존재하지 않는 주소를 조회하려고 하면 USER_ADDRESS_NOT_FOUND 예외가 발생한다") {
            every { userAddressRepository.findByIdAndUserId(userAddressId, userId) } returns Optional.empty()

            shouldThrow<CoreException> {
                userAddressService.getUserAddress(userId, userAddressId)
            }.errorCode shouldBe UserErrorCode.USER_ADDRESS_NOT_FOUND
        }

        test("다른 사용자의 주소를 조회하려고 하면 USER_ADDRESS_NOT_FOUND 예외가 발생한다") {
            val otherUserId = 999L

            every { userAddressRepository.findByIdAndUserId(userAddressId, userId) } returns Optional.empty()

            shouldThrow<CoreException> {
                userAddressService.getUserAddress(userId, userAddressId)
            }.errorCode shouldBe UserErrorCode.USER_ADDRESS_NOT_FOUND
        }
    }

    context("getDefaultUserAddress") {
        test("기본 배송지가 있는 경우 조회할 수 있다") {
            val defaultAddress = UserAddress(
                userId = userId,
                alias = "집",
                recipientName = "홍길동",
                recipientPhone = "01012345678",
                zipCode = "12345",
                address1 = "서울시 강남구",
                address2 = "테헤란로 123",
                isDefault = true,
            ).apply { id = 500L }

            every { userAddressRepository.findDefaultByUserId(userId) } returns Optional.of(defaultAddress)

            val result = userAddressService.findDefaultUserAddress(userId)

            result shouldNotBe null
            result?.addressId shouldBe 500L
            result?.alias shouldBe "집"
            result?.isDefault shouldBe true
        }

        test("기본 배송지가 없는 경우 null을 반환한다") {
            every { userAddressRepository.findDefaultByUserId(userId) } returns Optional.empty()

            val result = userAddressService.findDefaultUserAddress(userId)

            result shouldBe null
        }
    }

    context("register") {
        val registerRequest = RegisterUserAddressRequest(
            alias = "집",
            recipientName = "홍길동",
            recipientPhone = "010-1234-5678",
            zipCode = "12345",
            address1 = "서울시 강남구",
            address2 = "테헤란로 123",
            isDefault = false,
        )

        test("사용자 주소를 등록할 수 있다") {
            every { userRepository.findById(userId) } returns Optional.of(user)
            every { userAddressRepository.save(any()) } returns UserAddress(
                userId = userId,
                alias = registerRequest.alias,
                recipientName = registerRequest.recipientName,
                recipientPhone = registerRequest.recipientPhone,
                zipCode = registerRequest.zipCode,
                address1 = registerRequest.address1,
                address2 = registerRequest.address2,
                isDefault = registerRequest.isDefault,
            ).apply { id = 100L }

            val result = userAddressService.register(userId, registerRequest)

            result shouldBe 100L
            verify { userAddressRepository.save(any()) }
        }

        test("기본 주소로 등록시 기존 기본 주소를 해제한다") {
            val defaultRequest = registerRequest.copy(isDefault = true)
            val existingDefaultAddress = mockk<UserAddress>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { userAddressRepository.findDefaultByUserId(userId) } returns Optional.of(existingDefaultAddress)
            every { existingDefaultAddress.unsetAsDefault() } returns Unit
            every { userAddressRepository.save(any()) } returns UserAddress(
                userId = userId,
                alias = defaultRequest.alias,
                recipientName = defaultRequest.recipientName,
                recipientPhone = defaultRequest.recipientPhone,
                zipCode = defaultRequest.zipCode,
                address1 = defaultRequest.address1,
                address2 = defaultRequest.address2,
                isDefault = true,
            ).apply { id = 101L }

            val result = userAddressService.register(userId, defaultRequest)

            result shouldBe 101L
            verify { existingDefaultAddress.unsetAsDefault() }
            verify { userAddressRepository.save(any()) }
        }

        test("기본 주소가 없는 경우 기본 주소로 등록할 수 있다") {
            val defaultRequest = registerRequest.copy(isDefault = true)

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { userAddressRepository.findDefaultByUserId(userId) } returns Optional.empty()
            every { userAddressRepository.save(any()) } returns UserAddress(
                userId = userId,
                alias = defaultRequest.alias,
                recipientName = defaultRequest.recipientName,
                recipientPhone = defaultRequest.recipientPhone,
                zipCode = defaultRequest.zipCode,
                address1 = defaultRequest.address1,
                address2 = defaultRequest.address2,
                isDefault = true,
            ).apply { id = 102L }

            val result = userAddressService.register(userId, defaultRequest)

            result shouldBe 102L
            verify { userAddressRepository.save(any()) }
        }

        test("존재하지 않는 사용자의 주소를 등록하려고 하면 USER_NOT_FOUND 예외가 발생한다") {
            every { userRepository.findById(userId) } returns Optional.empty()

            shouldThrow<CoreException> {
                userAddressService.register(userId, registerRequest)
            }.errorCode shouldBe UserErrorCode.USER_NOT_FOUND
        }
    }

    context("update") {
        val userAddressId = 200L
        val existingUserAddress = mockk<UserAddress>()

        val updateRequest = UpdateUserAddressRequest(
            alias = "회사",
            recipientName = "김철수",
            recipientPhone = "010-9876-5432",
            zipCode = "54321",
            address1 = "부산시 해운대구",
            address2 = "센텀로 456",
            isDefault = false,
        )

        test("사용자 주소를 수정할 수 있다") {
            every { userRepository.findById(userId) } returns Optional.of(user)
            every { userAddressRepository.findById(userAddressId) } returns Optional.of(existingUserAddress)
            every { existingUserAddress.userId } returns userId
            every { existingUserAddress.update(any()) } just runs

            userAddressService.update(userId, userAddressId, updateRequest)

            verify { existingUserAddress.update(any()) }
        }

        test("기본 주소로 수정시 기존 기본 주소를 해제한다") {
            val defaultUpdateRequest = updateRequest.copy(isDefault = true)
            val existingDefaultAddress = mockk<UserAddress>()

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { userAddressRepository.findById(userAddressId) } returns Optional.of(existingUserAddress)
            every { existingUserAddress.userId } returns userId
            every { userAddressRepository.findDefaultByUserId(userId) } returns Optional.of(existingDefaultAddress)
            every { existingDefaultAddress.unsetAsDefault() } returns Unit
            every { existingUserAddress.update(any()) } just runs

            userAddressService.update(userId, userAddressId, defaultUpdateRequest)

            verify { existingDefaultAddress.unsetAsDefault() }
            verify { existingUserAddress.update(any()) }
        }

        test("존재하지 않는 사용자의 주소를 수정하려고 하면 USER_NOT_FOUND 예외가 발생한다") {
            every { userRepository.findById(userId) } returns Optional.empty()

            shouldThrow<CoreException> {
                userAddressService.update(userId, userAddressId, updateRequest)
            }.errorCode shouldBe UserErrorCode.USER_NOT_FOUND
        }

        test("존재하지 않는 주소를 수정하려고 하면 USER_ADDRESS_NOT_FOUND 예외가 발생한다") {
            every { userRepository.findById(userId) } returns Optional.of(user)
            every { userAddressRepository.findById(userAddressId) } returns Optional.empty()

            shouldThrow<CoreException> {
                userAddressService.update(userId, userAddressId, updateRequest)
            }.errorCode shouldBe UserErrorCode.USER_ADDRESS_NOT_FOUND
        }

        test("다른 사용자의 주소를 수정하려고 하면 UNAUTHORIZED_USER_ADDRESS_UPDATE 예외가 발생한다") {
            val otherUserId = 999L

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { userAddressRepository.findById(userAddressId) } returns Optional.of(existingUserAddress)
            every { existingUserAddress.userId } returns otherUserId

            shouldThrow<CoreException> {
                userAddressService.update(userId, userAddressId, updateRequest)
            }.errorCode shouldBe UserErrorCode.UNAUTHORIZED_USER_ADDRESS_UPDATE
        }
    }

    context("delete") {
        val userAddressId = 300L
        val existingUserAddress = mockk<UserAddress>()

        test("사용자 주소를 삭제할 수 있다") {
            every { userRepository.findById(userId) } returns Optional.of(user)
            every { userAddressRepository.findById(userAddressId) } returns Optional.of(existingUserAddress)
            every { existingUserAddress.userId } returns userId
            every { userAddressRepository.delete(existingUserAddress) } just runs

            userAddressService.delete(userId, userAddressId)

            verify { userAddressRepository.delete(existingUserAddress) }
        }

        test("존재하지 않는 사용자의 주소를 삭제하려고 하면 USER_NOT_FOUND 예외가 발생한다") {
            every { userRepository.findById(userId) } returns Optional.empty()

            shouldThrow<CoreException> {
                userAddressService.delete(userId, userAddressId)
            }.errorCode shouldBe UserErrorCode.USER_NOT_FOUND
        }

        test("존재하지 않는 주소를 삭제하려고 하면 USER_ADDRESS_NOT_FOUND 예외가 발생한다") {
            every { userRepository.findById(userId) } returns Optional.of(user)
            every { userAddressRepository.findById(userAddressId) } returns Optional.empty()

            shouldThrow<CoreException> {
                userAddressService.delete(userId, userAddressId)
            }.errorCode shouldBe UserErrorCode.USER_ADDRESS_NOT_FOUND
        }

        test("다른 사용자의 주소를 삭제하려고 하면 UNAUTHORIZED_USER_ADDRESS_DELETE 예외가 발생한다") {
            val otherUserId = 999L

            every { userRepository.findById(userId) } returns Optional.of(user)
            every { userAddressRepository.findById(userAddressId) } returns Optional.of(existingUserAddress)
            every { existingUserAddress.userId } returns otherUserId

            shouldThrow<CoreException> {
                userAddressService.delete(userId, userAddressId)
            }.errorCode shouldBe UserErrorCode.UNAUTHORIZED_USER_ADDRESS_DELETE
        }
    }
})
