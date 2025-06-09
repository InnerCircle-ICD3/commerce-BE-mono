package com.fastcampus.commerce.user.api.controller.request

import com.fastcampus.commerce.common.util.PhoneNumberUtil
import com.fastcampus.commerce.user.api.service.request.UpdateUserAddressRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateUserAddressApiRequest(
    @field:NotBlank(message = "배송지 별칭을 입력해주세요.")
    val alias: String,
    @field:NotBlank(message = "수령인을 입력해주세요.")
    val recipientName: String,
    @field:NotBlank(message = "수령인의 휴대폰 번호를 입력해주세요.")
    @field:Pattern(
        regexp = "^01(?:0|1|[6-9])-?(?:\\d{3}|\\d{4})-?\\d{4}$",
        message = "올바른 휴대폰 번호를 입력해주세요."
    )
    val recipientPhone: String,
    @field:NotBlank(message = "우편번호를 입력해주세요.")
    val zipCode: String,
    @field:NotBlank(message = "주소를 입력해주세요.")
    val address1: String,
    val address2: String? = null,
    val isDefault: Boolean = false,
) {
    fun toServiceRequest() =
        UpdateUserAddressRequest(
            alias = alias,
            recipientName = recipientName,
            recipientPhone = PhoneNumberUtil.removeHyphens(recipientPhone),
            zipCode = zipCode,
            address1 = address1,
            address2 = address2,
            isDefault = isDefault,
        )
}
