package com.fastcampus.commerce.user.api.service.request

import com.fastcampus.commerce.user.domain.model.UserAddressUpdater

data class UpdateUserAddressRequest(
    val alias: String,
    val recipientName: String,
    val recipientPhone: String,
    val zipCode: String,
    val address1: String,
    val address2: String?,
    val isDefault: Boolean
) {
    fun toUpdater()= UserAddressUpdater (
        alias = alias,
        recipientName = recipientName,
        recipientPhone = recipientPhone,
        zipCode = zipCode,
        address1 = address1,
        address2 = address2,
        isDefault = isDefault,
    )
}
