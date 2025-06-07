package com.fastcampus.commerce.cart.application

import com.fastcampus.commerce.cart.domain.entity.CartItem
import com.fastcampus.commerce.cart.infrastructure.repository.CartItemRepository
import com.fastcampus.commerce.common.policy.DeliveryPolicy
import com.fastcampus.commerce.cart.interfaces.CartUpdateRequest
import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.entity.SellingStatus
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
    private lateinit var deliveryPolicy: DeliveryPolicy

    @BeforeEach
    fun setUp() {
        cartItemRepository = mock(CartItemRepository::class.java)
        productReader = mock(ProductReader::class.java)
        deliveryPolicy = DeliveryPolicy()
        cartItemService = CartItemService(cartItemRepository, productReader, deliveryPolicy)
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

        val request = CartUpdateRequest(cartItemId, productId, requestQuantity)

        `when`(cartItemRepository.findByUserIdAndId(userId, cartItemId)).thenReturn(cartItem)
        `when`(productReader.getInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.save(any(CartItem::class.java))).thenReturn(cartItem)

        // When
        val result = cartItemService.updateCartItem(userId,request)

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

        val request = CartUpdateRequest(cartItemId, productId, requestQuantity)

        `when`(cartItemRepository.findByUserIdAndId(userId, cartItemId)).thenReturn(cartItem)
        `when`(productReader.getInventoryByProductId(productId)).thenReturn(inventory)
        `when`(cartItemRepository.save(any(CartItem::class.java))).thenReturn(cartItem)

        // When
        val result = cartItemService.updateCartItem(userId,request)

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

    @Test
    fun `해당 유저의 상품 전체 조회가 가능해야 한다`()  {
        // Given
        val userId = 1L
        val cartItems = listOf(
            CartItem(userId, 1L, 2),
            CartItem(userId, 2L, 3),
        )

        // Set IDs for cart items
        cartItems[0].id = 101L
        cartItems[1].id = 102L

        // Create products
        val product1 = Product(
            name = "Product 1",
            price = 10000,
            thumbnail = "thumbnail1.jpg",
            detailImage = "detail1.jpg",
        )
        product1.id = 1L
        product1.status = SellingStatus.ON_SALE

        val product2 = Product(
            name = "Product 2",
            price = 20000,
            thumbnail = "thumbnail2.jpg",
            detailImage = "detail2.jpg",
        )
        product2.id = 2L
        product2.status = SellingStatus.ON_SALE

        val inventory1 = Inventory(1L, 10)
        val inventory2 = Inventory(2L, 5)

        `when`(cartItemRepository.findAllByUserId(userId)).thenReturn(cartItems)
        `when`(productReader.getProductById(1L)).thenReturn(product1)
        `when`(productReader.getProductById(2L)).thenReturn(product2)
        `when`(productReader.getInventoryByProductId(1L)).thenReturn(inventory1)
        `when`(productReader.getInventoryByProductId(2L)).thenReturn(inventory2)

        // When
        val result = cartItemService.getCarts(userId)

        // Then
        assertEquals(2, result.cartItems.size)
        assertEquals(80000, result.totalPrice) // isavailable인 경우 (20000 * 3) = 60000
        assertEquals(0, result.deliveryPrice) // 총 가격이 30000원 이상이므로 배송비 무료

        val firstCartItem = result.cartItems[0]
        assertEquals(1L, firstCartItem.productId)
        assertEquals("Product 1", firstCartItem.productName)
        assertEquals(2, firstCartItem.quantity)
        assertEquals(10000, firstCartItem.price)
        assertEquals(10, firstCartItem.stockQuantity)
        assertEquals("thumbnail1.jpg", firstCartItem.thumbnail)
        assertEquals(true, firstCartItem.isAvailable) // Now true because status is UNAVAILABLE

        val secondCartItem = result.cartItems[1]
        assertEquals(2L, secondCartItem.productId)
        assertEquals("Product 2", secondCartItem.productName)
        assertEquals(3, secondCartItem.quantity)
        assertEquals(20000, secondCartItem.price)
        assertEquals(5, secondCartItem.stockQuantity)
        assertEquals("thumbnail2.jpg", secondCartItem.thumbnail)
        assertEquals(true, secondCartItem.isAvailable) // Now true because status is UNAVAILABLE
    }

    @Test
    fun `장바구니가 비어있을 때 빈 리스트와 0원을 반환해야 한다`() {
        // Given
        val userId = 1L
        `when`(cartItemRepository.findAllByUserId(userId)).thenReturn(emptyList())

        // When
        val result = cartItemService.getCarts(userId)

        // Then
        assertEquals(0, result.cartItems.size)
        assertEquals(0, result.totalPrice)
        assertEquals(0, result.deliveryPrice) // 빈 장바구니는 배송비 무료
    }

    @Test
    fun `UNAVAILABLE 상품은 총 가격 계산에서 제외되어야 한다`() {
        // Given
        val userId = 1L
        val cartItems = listOf(
            CartItem(userId, 1L, 2), // 10,000원 x 2 = 20,000원 (AVAILABLE)
            CartItem(userId, 2L, 3), // 5,000원 x 3 = 15,000원 (UNAVAILABLE)
        )

        // CartItem ID 설정
        cartItems[0].id = 101L
        cartItems[1].id = 102L

        val product1 = Product(
            name = "Product 1",
            price = 10000,
            thumbnail = "thumbnail1.jpg",
            detailImage = "detail1.jpg",
        )

        val product2 = Product(
            name = "Product 2",
            price = 20000,
            thumbnail = "thumbnail2.jpg",
            detailImage = "detail2.jpg",
        )

        product1.id = 1L
        product2.id = 2L

        // product1은 AVAILABLE, product2는 UNAVAILABLE로 설정
        product1.status = SellingStatus.ON_SALE
        product2.status = SellingStatus.UNAVAILABLE

        val inventory1 = Inventory(1L, 10)
        val inventory2 = Inventory(2L, 5)

        // Mock 설정 추가
        `when`(cartItemRepository.findAllByUserId(userId)).thenReturn(cartItems)
        `when`(productReader.getProductById(1L)).thenReturn(product1)
        `when`(productReader.getProductById(2L)).thenReturn(product2)
        `when`(productReader.getInventoryByProductId(1L)).thenReturn(inventory1)
        `when`(productReader.getInventoryByProductId(2L)).thenReturn(inventory2)

        // When
        val result = cartItemService.getCarts(userId)

        // Then
        assertEquals(20000, result.totalPrice) // UNAVAILABLE 상품 제외
        assertEquals(3000, result.deliveryPrice) // 30,000원 미만이므로 배송비 부과
    }
}
