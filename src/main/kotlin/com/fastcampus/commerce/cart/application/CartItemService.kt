package com.fastcampus.commerce.cart.application

import com.fastcampus.commerce.cart.domain.entity.CartItem
import com.fastcampus.commerce.cart.infrastructure.repository.CartItemRepository
import com.fastcampus.commerce.cart.interfaces.CartCreateResponse
import com.fastcampus.commerce.product.application.ProductReader
import com.fastcampus.commerce.product.domain.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartItemService(
    private val cartItemRepository: CartItemRepository,
    private val productReader: ProductReader,
) {
    @Transactional
    fun addToCart(userId: Long, productId: Long, quantity: Int): CartCreateResponse {
        val inventory = productReader.getInventoryByProductId(productId)

        val stockQuantity = inventory.quantity
        var requiresQuantityAdjustment = false
        var finalQuantity: Int  // 실제로 장바구니에 담긴 최종 수량

        val existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)

        if (existingCartItem != null) {
            // 기존 수량 + 추가 수량
            val desiredQuantity = existingCartItem.quantity + quantity

            if (desiredQuantity > stockQuantity) {
                finalQuantity = stockQuantity
                requiresQuantityAdjustment = true
            } else {
                finalQuantity = desiredQuantity
            }

            existingCartItem.quantity = finalQuantity
            cartItemRepository.save(existingCartItem)
        } else {
            // 새로운 상품 추가
            if (quantity > stockQuantity) {
                finalQuantity = stockQuantity
                requiresQuantityAdjustment = true
            } else {
                finalQuantity = quantity
            }

            val cartItem = CartItem(
                userId = userId,
                productId = productId,
                quantity = finalQuantity,
            )
            cartItemRepository.save(cartItem)
        }

        return CartCreateResponse(
            quantity = finalQuantity,
            stockQuantity = stockQuantity,
            requiresQuantityAdjustment = requiresQuantityAdjustment,
        )
    }
}
