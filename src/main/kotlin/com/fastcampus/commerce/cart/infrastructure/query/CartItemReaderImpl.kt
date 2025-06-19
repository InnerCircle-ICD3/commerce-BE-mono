package com.fastcampus.commerce.cart.infrastructure.query

import com.fastcampus.commerce.cart.application.query.CartItemReader
import com.fastcampus.commerce.cart.application.query.dto.CartItemDto
import com.fastcampus.commerce.cart.infrastructure.repository.CartItemRepository
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.order.application.query.ProductSnapshotReader
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.product.domain.service.ProductReader
import org.springframework.stereotype.Component

@Component
class CartItemReaderImpl(
    private val cartItemRepository: CartItemRepository,
) : CartItemReader {
    override fun readCartItems(userId: Long, cartItemIds: Set<Long>): List<CartItemDto> {
        val cartItems = cartItemRepository.getAllByUserIdAndIdIn(userId, cartItemIds)
        if (cartItems.isEmpty()) {
            throw CoreException(OrderErrorCode.CART_ITEM_NOT_MATCH)
        }
        return cartItems.map { cartItem ->
            CartItemDto(
                cartItemId = cartItem.id!!,
                productId = cartItem.productId,
                quantity = cartItem.quantity,
            )
        }
    }
}
