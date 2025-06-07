package com.fastcampus.commerce.cart.interfaces

import com.fastcampus.commerce.cart.application.CartItemService
import org.springframework.web.bind.annotation.GetMapping
//import com.fastcampus.commerce.user.domain.entity.User
//import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CartItemController(
    private val cartItemService: CartItemService,
) {
    @GetMapping("/carts")
    fun getCarts(
//        @AuthenticationPrincipal user: User,
    ): CartRetrievesResponse {
        return cartItemService.getCarts(1L)
    }

    @PostMapping("/cart/items")
    fun addToCart(
        @RequestBody request: CartCreateRequest,
//        @AuthenticationPrincipal user: User
    ): CartCreateResponse {
        val response = cartItemService.addToCart(1L, request.productId, request.quantity)
        return response
    }

    @PatchMapping("/cart/items")
    fun updateCartItem(
//        @AuthenticationPrincipal user: User,
        @RequestBody request: CartUpdateRequest,
    ): CartUpdateResponse {
        val cartResponse = cartItemService.updateCartItem(1L,request)
        return cartResponse
    }
}
