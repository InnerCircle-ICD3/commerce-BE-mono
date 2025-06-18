package com.fastcampus.commerce.order.interfaces.response

import com.fastcampus.commerce.cart.application.query.dto.CartItemDto
import com.fastcampus.commerce.common.response.EnumResponse
import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.entity.Product

data class PrepareOrderApiResponse(
    val cartItemIds: Set<Long>,
    val itemsSubtotal: Int,
    val shippingFee: Int,
    val finalTotalPrice: Int,
    val items: List<PrepareOrderItemApiResponse>,
    val shippingInfo: PrepareOrderShippingInfoApiResponse? = null,
    val paymentMethod: List<EnumResponse>,
)

data class PrepareOrderItemApiResponse(
    val cartItemId: Long,
    val productId: Long,
    val name: String,
    val thumbnail: String,
    val unitPrice: Int,
    val quantity: Int,
    val itemSubtotal: Int,
) {
    companion object {
        fun of(cartItem: CartItemDto, product: Product) =
            PrepareOrderItemApiResponse (
                cartItemId = cartItem.cartItemId,
                productId = product.id!!,
                name = product.name,
                thumbnail = product.thumbnail,
                unitPrice = product.price,
                quantity = cartItem.quantity,
                itemSubtotal = product.price * cartItem.quantity,
            )
    }
}

data class PrepareOrderShippingInfoApiResponse(
    val recipientName: String,
    val recipientPhone: String,
    val zipCode: String,
    val addressId: Long,
    val address1: String,
    val address2: String? = null,
)
