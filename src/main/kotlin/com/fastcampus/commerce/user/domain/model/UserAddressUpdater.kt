package com.fastcampus.commerce.user.domain.model

data class UserAddressUpdater(
    val alias: String,
    val recipientName: String,
    val recipientPhone: String,
    val zipCode: String,
    val address1: String,
    val address2: String?,
    val isDefault: Boolean,
)
