package com.fastcampus.commerce.user.api.controller.response

import com.fastcampus.commerce.user.api.service.response.UserAddressResponse

data class DefaultAddressApiResponse(
    val hasDefault: Boolean,
    val address: UserAddressApiResponse? = null,
) {
    companion object {
        fun from(response: UserAddressResponse?): DefaultAddressApiResponse {
            return if (response != null) {
                DefaultAddressApiResponse(
                    hasDefault = true,
                    address = UserAddressApiResponse.from(response),
                )
            } else {
                DefaultAddressApiResponse(
                    hasDefault = false,
                    address = null,
                )
            }
        }
    }
}
