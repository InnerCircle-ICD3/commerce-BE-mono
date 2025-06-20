package com.fastcampus.commerce.cart.interfaces

import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.cart.application.CartItemService
import com.fastcampus.commerce.user.domain.enums.UserRole
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
    fun getCarts(
        @WithRoles([UserRole.USER]) user: LoginUser,
    ): CartRetrievesResponse {
        return cartItemService.getCarts(user.id)
    }

    @PostMapping("/cart-items")
    fun addToCart(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @RequestBody @Valid request: CartCreateRequest,
    ): CartCreateResponse {
        val response = cartItemService.addToCart(user.id, request.productId, request.quantity)
        return response
    }

    @PatchMapping("/cart-items/{cartItemId}")
    fun updateCartItem(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @PathVariable cartItemId: Long,
        @RequestBody @Valid request: CartUpdateRequest,
    ): CartUpdateResponse {
        val cartResponse = cartItemService.updateCartItem(user.id, cartItemId, request)
        return cartResponse
    }

    @DeleteMapping("/cart-items")
    fun deleteCartItems(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @RequestParam(value = "cartItemIds", required = true) cartItemIds: List<Long>,
    ): CartDeleteResponse {
        val deletedCount = cartItemService.deleteCartItems(cartItemIds)
        val response = CartDeleteResponse("Successfully deleted $deletedCount cart items")
        return response
    }
}
