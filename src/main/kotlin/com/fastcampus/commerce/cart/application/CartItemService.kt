package com.fastcampus.commerce.cart.application

import com.fastcampus.commerce.cart.domain.entity.CartItem
import com.fastcampus.commerce.cart.domain.error.CartErrorCode
import com.fastcampus.commerce.cart.infrastructure.repository.CartItemRepository
import com.fastcampus.commerce.cart.interfaces.CartCreateResponse
import com.fastcampus.commerce.cart.interfaces.CartItemRetrieve
import com.fastcampus.commerce.cart.interfaces.CartRetrievesResponse
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.cart.interfaces.CartItemRetrieve
import com.fastcampus.commerce.cart.interfaces.CartRetrievesResponse
import com.fastcampus.commerce.common.policy.DeliveryPolicy
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.cart.interfaces.CartUpdateRequest
import com.fastcampus.commerce.cart.interfaces.CartUpdateResponse
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.service.ProductReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.fastcampus.commerce.common.error.CoreException
@Service
class CartItemService(
    private val cartItemRepository: CartItemRepository,
    private val productReader: ProductReader,
    private val deliveryPolicy: DeliveryPolicy,
) {
    fun getCarts(userId: Long): CartRetrievesResponse  {
        val cartItems = cartItemRepository.findAllByUserId(userId) ?: emptyList()

        if (cartItems.isEmpty()) {
            return CartRetrievesResponse(
                totalPrice = 0,
                deliveryPrice = 0,
                cartItems = emptyList(),
            )
        }

        val cartItemRetrieveList = cartItems.map { cartItem ->
            val product = productReader.getProductById(cartItem.productId)
            val inventory = productReader.getInventoryByProductId(cartItem.productId)

            val isAvailable = product.status != SellingStatus.UNAVAILABLE

            CartItemRetrieve(
                cartItemId = cartItem.id!!,
                productId = product.id!!,
                productName = product.name,
                quantity = cartItem.quantity,
                price = product.price,
                stockQuantity = inventory.quantity,
                thumbnail = product.thumbnail,
                isAvailable = isAvailable,
            )
        }

        // 구매 가능한 상품들의 총 가격 계산
        val totalPrice = cartItemRetrieveList
            .filter { it.isAvailable }
            .sumOf { it.price * it.quantity }

        val deliveryPrice = deliveryPolicy.calculateDeliveryFee(totalPrice)

        return CartRetrievesResponse(
            totalPrice = totalPrice,
            deliveryPrice = deliveryPrice,
            cartItems = cartItemRetrieveList,
        )
    }

    @Transactional
    fun addToCart(userId: Long, productId: Long, quantity: Int): CartCreateResponse {
        val inventory = productReader.getInventoryByProductId(productId)

        val stockQuantity = inventory.quantity
        var requiresQuantityAdjustment = false
        var finalQuantity: Int // 실제로 장바구니에 담긴 최종 수량

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

    @Transactional
    fun updateCartItem(userId: Long,request: CartUpdateRequest): CartUpdateResponse {
        var requireQuantityAdjustment = false
        val cartItem = cartItemRepository.findByUserIdAndId(userId, request.cartId)
            ?: throw CoreException(CartErrorCode.CART_ITEMS_NOT_FOUND)

        val inventory = productReader.getInventoryByProductId(cartItem.productId)

        if (request.quantity > inventory.quantity) {
            cartItem.quantity = inventory.quantity
            requireQuantityAdjustment = true
        } else {
            cartItem.quantity = request.quantity
        }

        cartItemRepository.save(cartItem)

        return CartUpdateResponse(
            userId = userId,
            productId = cartItem.productId,
            quantity = cartItem.quantity,
            stockQuantity = inventory.quantity,
            requiresQuantityAdjustment = requireQuantityAdjustment,
        )
    }

    @Transactional
    fun deleteCartItems(productIds: List<Long>): Int {
        if (productIds.isEmpty()) {
            throw CoreException(CartErrorCode.EMPTY_PRODUCT_IDS)
        }

        val cartItems = cartItemRepository.findAllById(productIds)
        if (cartItems.isEmpty()) {
            throw CoreException(CartErrorCode.CART_ITEMS_NOT_FOUND)
        }

        cartItemRepository.softDeleteByIds(productIds)

        return cartItems.size
    }
}
