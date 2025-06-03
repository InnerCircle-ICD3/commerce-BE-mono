package com.fastcampus.commerce.cart.application

import com.fastcampus.commerce.cart.domain.entity.CartItem
import com.fastcampus.commerce.cart.infrastructure.repository.CartItemRepository
import com.fastcampus.commerce.cart.interfaces.CartCreateResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartItemService(
    private val cartItemRepository: CartItemRepository,
    private val inventoryService: InventoryService,
) {
    @Transactional
    fun addToCart(userId: Long, productId: Long, quantity: Int): CartCreateResponse {
        val userIdValue = userId
        val productIdValue = productId

        // 임시로 InventoryRepository 서비스를 만들어 구현했습니다.
        // 추후 재고 기능이 구현 되면 임시 서비스를 삭제하고 붙일 예정입니다.
        val inventory = inventoryService.findInventoryByProductId(productId)
            ?: throw IllegalArgumentException("Product not found in inventory")

        val stockQuantity = inventory.quantity
        var requiresQuantityAdjustment = false
        var finalQuantity: Int  // 실제로 장바구니에 담긴 최종 수량

        val existingCartItem = cartItemRepository.findByUserIdAndProductId(userIdValue, productIdValue)

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
                userId = userIdValue,
                productId = productIdValue,
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
