package com.fastcampus.commerce.order.interfaces.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class OrderApiRequest(
    @field:NotEmpty(message = "주문할 장바구니 아이디가 누락되었습니다.")
    val cartItemIds: Set<Long>,
    @field:NotNull(message = "배송정보가 누락되었습니다.")
    val shippingInfo: OrderShippingInfoApiRequest,
    @field:NotBlank(message = "결제방식이 누락되었습니다.")
    val paymentMethod: String,
)

data class OrderShippingInfoApiRequest(
    @field:NotBlank(message = "받는사람 이름이 누락되었습니다.")
    val recipientName: String,
    @field:NotBlank(message = "받는사람 연락처가 누락되었습니다.")
    val recipientPhone: String,
    @field:NotBlank(message = "우편번호가 누락되었습니다.")
    val zipCode: String,
    @field:NotBlank(message = "주소가 누락되었습니다.")
    val address1: String,
    val address2: String? = null,
    val deliveryMessage: String? = null,
)
