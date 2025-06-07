package com.fastcampus.commerce.cart.interfaces

import com.fastcampus.commerce.cart.application.CartItemService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CartItemController(
    private val cartItemService: CartItemService,
) {
    @GetMapping("/carts")
    fun getCarts(): CartRetrievesResponse {
        val userId = 1L
        return cartItemService.getCarts(userId)
    }

    @PostMapping("/cart/items")
    fun addToCart(
        @RequestBody request: CartCreateRequest,
    ): CartCreateResponse {
        val userId = 1L
        val response = cartItemService.addToCart(userId, request.productId, request.quantity)
        return response
    }

    @PatchMapping("/cart/items")
    fun updateCartItem(
        @RequestBody request: CartUpdateRequest,
    ): CartUpdateResponse {
        val userId = 1L
        val cartResponse = cartItemService.updateCartItem(userId, request)
        return cartResponse
    }

    @PostMapping("/carts/delete")
    fun deleteCartItems(
        @RequestBody request: CartDeleteRequest,
    ): CartDeleteResponse {
        val deletedCount = cartItemService.deleteCartItems(request.productIds)
        val response = CartDeleteResponse("Successfully deleted $deletedCount cart items")
        return response
    }
}
