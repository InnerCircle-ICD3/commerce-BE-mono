package com.fastcampus.commerce.cart.interfaces

import com.fastcampus.commerce.cart.application.CartItemService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CartItemController(
    private val cartItemService: CartItemService,
) {
    @PostMapping("/cart/items")
    fun addToCart(
        @RequestParam userId: Long,
        @RequestBody request: CartCreateRequest,
    ): CartCreateResponse {
        val response = cartItemService.addToCart(userId, request.productId, request.quantity)
        return response
    }
}
