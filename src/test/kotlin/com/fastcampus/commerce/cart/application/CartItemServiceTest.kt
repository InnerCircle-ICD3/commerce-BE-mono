package com.fastcampus.commerce.cart.application

import com.fastcampus.commerce.cart.domain.entity.CartItem
import com.fastcampus.commerce.cart.infrastructure.repository.CartItemRepository
import com.fastcampus.commerce.cart.application.InventoryService
import com.fastcampus.commerce.product.domain.entity.Inventory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class CartItemServiceTest {

    private lateinit var cartItemRepository: CartItemRepository
    private lateinit var inventoryService: InventoryService
    private lateinit var cartItemService: CartItemService

    @BeforeEach
    fun setUp() {
        cartItemRepository = mock(CartItemRepository::class.java)
        inventoryService = mock(InventoryService::class.java)
        cartItemService = CartItemService(cartItemRepository, inventoryService)
    }

    @Test
    fun `장바구니에 새로운 상품 추가`() {
        // Given
        val userId = 1L
        val productId = 2L
        val quantity = 5
        val inventory = Inventory(productId, 10)

        `when`(inventoryService.findInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(null)

        // When
        val result = cartItemService.addToCart(userId, productId, quantity)

        // Then
        verify(cartItemRepository).save(any(CartItem::class.java))
        assertEquals(10, result.stockQuantity)
        assertEquals(false, result.requiresQuantityAdjustment)
    }

    @Test
    fun `기존 상품이 있을 경우 장바구니에 상품 추가시 수량 더하기`() {
        // Given
        val userId = 1L
        val productId = 2L
        val quantity = 5
        val existingQuantity = 3
        val inventory = Inventory(productId, 10)
        val existingCartItem = CartItem(userId, productId, existingQuantity)

        `when`(inventoryService.findInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(existingCartItem)

        // When
        val result = cartItemService.addToCart(userId, productId, quantity)

        // Then
        verify(cartItemRepository).save(any(CartItem::class.java))
        assertEquals(10, result.stockQuantity)
        assertEquals(false, result.requiresQuantityAdjustment)
    }

    @Test
    fun `추가한 상품 수량이 재고 수량을 초과할 경우 재고 수량 만큼만 추가된다`() {
        // Given
        val userId = 1L
        val productId = 2L
        val quantity = 15
        val inventory = Inventory(productId, 10)

        `when`(inventoryService.findInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(null)

        // When
        val result = cartItemService.addToCart(userId, productId, quantity)

        // Then
        verify(cartItemRepository).save(any(CartItem::class.java))
        assertEquals(10, result.stockQuantity)
        assertEquals(true, result.requiresQuantityAdjustment)
    }
}
