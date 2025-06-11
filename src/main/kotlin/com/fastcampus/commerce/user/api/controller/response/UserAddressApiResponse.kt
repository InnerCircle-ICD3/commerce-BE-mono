package com.fastcampus.commerce.user.api.controller.response

import com.fastcampus.commerce.user.api.service.response.UserAddressResponse

data class UserAddressApiResponse(
    val addressId: Long,
    val alias: String,
    val recipientName: String,
    val recipientPhone: String,
    val zipCode: String,
    val address1: String,
    val address2: String? = null,
    val isDefault: Boolean,
) {
    companion object {
        fun from(response: UserAddressResponse) =
            UserAddressApiResponse(
                addressId = response.addressId,
                alias = response.alias,
                recipientName = response.recipientName,
                recipientPhone = response.recipientPhone,
                zipCode = response.zipCode,
                address1 = response.address1,
                address2 = response.address2,
                isDefault = response.isDefault,
            )
    }
}
