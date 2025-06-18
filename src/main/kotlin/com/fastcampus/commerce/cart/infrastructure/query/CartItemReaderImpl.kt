package com.fastcampus.commerce.cart.infrastructure.query

import com.fastcampus.commerce.cart.application.query.CartItemReader
import com.fastcampus.commerce.cart.application.query.dto.CartItemDto
import com.fastcampus.commerce.cart.infrastructure.repository.CartItemRepository
import com.fastcampus.commerce.order.application.query.ProductSnapshotReader
import com.fastcampus.commerce.product.domain.service.ProductReader
import org.springframework.stereotype.Component

@Component
class CartItemReaderImpl(
    private val cartItemRepository: CartItemRepository,
) : CartItemReader {
    override fun readCartItems(userId: Long, cartItemIds: Set<Long>): List<CartItemDto> {
        val cartItems = cartItemRepository.findAllByUserId(userId)!!.filter { cartItemIds.contains(it.id) }
        return cartItems.map { cartItem ->
            CartItemDto(
                cartItemId = cartItem.id!!,
                productId = cartItem.productId,
                quantity = cartItem.quantity,
            )
        }
    }
}
