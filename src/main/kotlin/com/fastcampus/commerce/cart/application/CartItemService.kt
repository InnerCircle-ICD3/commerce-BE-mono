package com.fastcampus.commerce.cart.application

import com.fastcampus.commerce.cart.domain.entity.CartItem
import com.fastcampus.commerce.cart.infrastructure.repository.CartItemRepository
import com.fastcampus.commerce.cart.interfaces.CartCreateResponse
import com.fastcampus.commerce.cart.application.InventoryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartItemService(
    private val cartItemRepository: CartItemRepository,
    private val inventoryService: InventoryService
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
        val requiresQuantityAdjustment = quantity > stockQuantity

        val actualQuantity = if (requiresQuantityAdjustment) stockQuantity else quantity
        val existingCartItem = cartItemRepository.findByUserIdAndProductId(userIdValue, productIdValue)
        if (existingCartItem != null) {
            val updatedQuantity = existingCartItem.quantity + actualQuantity

            existingCartItem.quantity = updatedQuantity
            cartItemRepository.save(existingCartItem)
        } else {
            val cartItem = CartItem(
                userId = userIdValue,
                productId = productIdValue,
                quantity = actualQuantity
            )
            cartItemRepository.save(cartItem)
        }

        return CartCreateResponse(
            stockQuantity = stockQuantity,
            requiresQuantityAdjustment = requiresQuantityAdjustment
        )
    }
}
