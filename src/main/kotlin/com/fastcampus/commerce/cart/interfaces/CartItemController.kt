package com.fastcampus.commerce.cart.interfaces

import com.fastcampus.commerce.cart.application.CartItemService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
class CartItemController(
    private val cartItemService: CartItemService,
) {
    @GetMapping("/cart-items")
    fun getCarts(): CartRetrievesResponse {
        val userId = 1L
        return cartItemService.getCarts(userId)
    }

    @PostMapping("/cart-items")
    fun addToCart(
        @RequestBody @Valid request: CartCreateRequest,
    ): CartCreateResponse {
        val userId = 1L
        val response = cartItemService.addToCart(userId, request.productId, request.quantity)
        return response
    }

    @PatchMapping("/cart-items/{cartItemId}")
    fun updateCartItem(
        @PathVariable cartItemId: Long,
        @RequestBody @Valid request: CartUpdateRequest,
    ): CartUpdateResponse {
        val userId = 1L
        val cartResponse = cartItemService.updateCartItem(userId, cartItemId,request)
        return cartResponse
    }

    @DeleteMapping("/cart-items")
    fun deleteCartItems(
        @RequestParam cartItemIds: List<Long>
    ): CartDeleteResponse {
        val deletedCount = cartItemService.deleteCartItems(cartItemIds)
        val response = CartDeleteResponse("Successfully deleted $deletedCount cart items")
        return response
    }
}
