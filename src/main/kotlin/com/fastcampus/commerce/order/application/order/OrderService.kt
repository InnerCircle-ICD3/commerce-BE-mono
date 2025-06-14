package com.fastcampus.commerce.order.application.order

import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.repository.OrderItemRepository
import com.fastcampus.commerce.order.domain.repository.OrderRepository
import com.fastcampus.commerce.order.domain.service.OrderNumberGenerator
import com.fastcampus.commerce.order.interfaces.request.OrderApiRequest
import com.fastcampus.commerce.order.interfaces.response.OrderApiResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    // private val cartItemReader: CartItemReader, // 장바구니 조회용 컴포넌트
    private val orderNumberGenerator: OrderNumberGenerator,
) {
    @Transactional
    fun createOrder(userId: Long, request: OrderApiRequest): OrderApiResponse {
        // 1. 장바구니 아이템 정보 조회 (상품ID, 수량, 가격)
        /*val cartItems = cartItemReader.readCartItems(userId, request.cartItemIds)

        // 2. 총 주문 금액 계산
        val totalAmount = cartItems.sumOf { it.unitPrice * it.quantity }*/

        // 난수 생성
        val orderId = 1L

        // 3. 주문 번호 생성
        val orderNumber = orderNumberGenerator.generate(orderId)

        // 4. Order Entity 생성 및 저장
        val order = Order(
            orderNumber = orderNumber,
            userId = userId,
            // totalAmount = totalAmount,
            totalAmount = 0,
            recipientName = request.shippingInfo.recipientName,
            recipientPhone = request.shippingInfo.recipientPhone,
            zipCode = request.shippingInfo.zipCode,
            address1 = request.shippingInfo.address1,
            address2 = request.shippingInfo.address2,
            deliveryMessage = request.shippingInfo.deliveryMessage,
            status = OrderStatus.WAITING_FOR_PAYMENT, // 상태 초기화
        )
        orderRepository.save(order)

        // 5. OrderItem Entity 생성 및 저장
        /*val orderItems = cartItems.map { cartItem ->
            OrderItem(
                orderId = order.id!!,
                productSnapshotId = cartItem.productSnapshotId,
                quantity = cartItem.quantity,
                unitPrice = cartItem.unitPrice
            )
        }
        orderItemRepository.saveAll(orderItems)*/

        // 6. 응답 반환
        return OrderApiResponse(orderNumber)
    }
}
