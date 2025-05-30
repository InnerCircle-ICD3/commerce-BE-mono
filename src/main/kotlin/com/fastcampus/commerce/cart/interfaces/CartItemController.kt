package com.fastcampus.commerce.cart.interfaces

import com.fastcampus.commerce.cart.application.CartItemService
import com.fastcampus.commerce.cart.domain.entity.CartItem
import com.fastcampus.commerce.common.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cart/items")
class CartItemController(
    private val cartItemService: CartItemService
) {
    @GetMapping
    fun getCartItems(@RequestParam userId: Long): ResponseEntity<ApiResponse<List<CartItem>>> {
        val cartItems = cartItemService.getCartItems(userId)
        return ResponseEntity.ok(ApiResponse.success(cartItems))
    }

    @PostMapping
    fun addToCart(
        @RequestParam userId: Long,
        @RequestBody request: CartCreateRequest
    ): ResponseEntity<ApiResponse<CartCreateResponse>> {
        val response = cartItemService.addToCart(userId, request.productId, request.quantity)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
