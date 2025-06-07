package com.fastcampus.commerce.cart.application

import com.fastcampus.commerce.cart.domain.entity.CartItem
import com.fastcampus.commerce.cart.infrastructure.repository.CartItemRepository
import com.fastcampus.commerce.cart.interfaces.CartUpdateRequest
import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.service.ProductReader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class CartItemServiceTest {
    private lateinit var cartItemRepository: CartItemRepository
    private lateinit var productReader: ProductReader
    private lateinit var cartItemService: CartItemService

    @BeforeEach
    fun setUp() {
        cartItemRepository = mock(CartItemRepository::class.java)
        productReader = mock(ProductReader::class.java)
        cartItemService = CartItemService(cartItemRepository, productReader)
    }

    @Test
    fun `장바구니에 새로운 상품 추가`() {
        // Given
        val userId = 1L
        val productId = 2L
        val quantity = 5
        val inventory = Inventory(productId, 10)

        `when`(productReader.getInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(null)

        // When
        val result = cartItemService.addToCart(userId, productId, quantity)

        // Then
        verify(cartItemRepository).save(any(CartItem::class.java))
        assertEquals(5, result.quantity)
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

        `when`(productReader.getInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(existingCartItem)

        // When
        val result = cartItemService.addToCart(userId, productId, quantity)

        // Then
        verify(cartItemRepository).save(any(CartItem::class.java))
        assertEquals(8, result.quantity)
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

        `when`(productReader.getInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(null)

        // When
        val result = cartItemService.addToCart(userId, productId, quantity)

        // Then
        verify(cartItemRepository).save(any(CartItem::class.java))
        assertEquals(10, result.quantity)
        assertEquals(10, result.stockQuantity)
        assertEquals(true, result.requiresQuantityAdjustment)
    }

    @Test
    fun `기존 상품 수량과 추가 수량의 합이 재고를 초과하는 경우`() {
        // Given
        val userId = 1L
        val productId = 2L
        val quantity = 8
        val existingQuantity = 5
        val inventory = Inventory(productId, 10)
        val existingCartItem = CartItem(userId, productId, existingQuantity)

        `when`(productReader.getInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(existingCartItem)

        // When
        val result = cartItemService.addToCart(userId, productId, quantity)

        // Then
        val cartItemCaptor = ArgumentCaptor.forClass(CartItem::class.java)
        verify(cartItemRepository).save(cartItemCaptor.capture())

        assertEquals(10, cartItemCaptor.value.quantity) // 재고 수량만큼만
        assertEquals(10, result.quantity)
        assertEquals(10, result.stockQuantity)
        assertEquals(true, result.requiresQuantityAdjustment)
    }

    @Test
    fun `장바구니 아이템 수량 업데이트 - 재고 수량 이내`() {
        // Given
        val userId = 1L
        val cartItemId = 1L
        val productId = 2L
        val requestQuantity = 5
        val inventoryQuantity = 10

        val cartItem = CartItem(userId, productId, 3)
        cartItem.id = cartItemId

        val inventory = Inventory(productId, inventoryQuantity)
        inventory.id = productId

        val request = CartUpdateRequest(cartItemId, userId, productId, requestQuantity)

        `when`(cartItemRepository.findByUserIdAndId(userId, cartItemId)).thenReturn(cartItem)
        `when`(productReader.getInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.save(any(CartItem::class.java))).thenReturn(cartItem)

        // When
        val result = cartItemService.updateCartItem(request)

        // Then
        verify(cartItemRepository).save(any(CartItem::class.java))

        // cartItem의 quantity가 실제로 변경되었는지 확인
        assertEquals(requestQuantity, cartItem.quantity)

        assertEquals(userId, result.userId)
        assertEquals(productId, result.productId)
        assertEquals(requestQuantity, result.quantity)
        assertEquals(inventoryQuantity, result.stockQuantity)
        assertFalse(result.requiresQuantityAdjustment)
    }

    @Test
    fun `장바구니 아이템 수량 업데이트 - 재고 수량 초과`() {
        // Given
        val userId = 1L
        val cartItemId = 1L
        val productId = 2L
        val requestQuantity = 15
        val inventoryQuantity = 10

        val cartItem = CartItem(userId, productId, 3)
        cartItem.id = cartItemId

        val inventory = Inventory(productId, inventoryQuantity)
        inventory.id = productId

        val request = CartUpdateRequest(cartItemId, userId, productId, requestQuantity)

        `when`(cartItemRepository.findByUserIdAndId(userId, cartItemId)).thenReturn(cartItem)
        `when`(productReader.getInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.save(any(CartItem::class.java))).thenReturn(cartItem)

        // When
        val result = cartItemService.updateCartItem(request)

        // Then
        verify(cartItemRepository).save(any(CartItem::class.java))

        // 재고 초과시 inventory quantity로 조정되었는지 확인
        assertEquals(inventoryQuantity, cartItem.quantity)

        assertEquals(userId, result.userId)
        assertEquals(productId, result.productId)
        assertEquals(inventoryQuantity, result.quantity)
        assertEquals(inventoryQuantity, result.stockQuantity)
        assertTrue(result.requiresQuantityAdjustment)
    }
}
