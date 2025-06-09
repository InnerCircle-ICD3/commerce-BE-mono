package com.fastcampus.commerce.user.api.service.response

import com.fastcampus.commerce.common.util.PhoneNumberUtil
import com.fastcampus.commerce.user.domain.entity.UserAddress

data class UserAddressResponse(
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
        fun from(userAddress: UserAddress) =
            UserAddressResponse(
                addressId = userAddress.id!!,
                alias = userAddress.alias,
                recipientName = userAddress.recipientName,
                recipientPhone = PhoneNumberUtil.addHyphens(userAddress.recipientPhone),
                zipCode = userAddress.zipCode,
                address1 = userAddress.address1,
                address2 = userAddress.address2,
                isDefault = userAddress.isDefault,
            )

    }
}
