package com.fastcampus.commerce.order.application.order

import com.fastcampus.commerce.cart.application.query.CartItemReader
import com.fastcampus.commerce.common.response.EnumResponse
import com.fastcampus.commerce.order.application.query.ProductSnapshotReader
import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderItem
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.repository.OrderItemRepository
import com.fastcampus.commerce.order.domain.repository.OrderRepository
import com.fastcampus.commerce.order.domain.service.OrderNumberGenerator
import com.fastcampus.commerce.order.interfaces.request.OrderApiRequest
import com.fastcampus.commerce.order.interfaces.response.OrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderItemApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderShippingInfoApiResponse
import com.fastcampus.commerce.payment.domain.entity.PaymentMethod
import com.fastcampus.commerce.payment.domain.service.PaymentReader
import com.fastcampus.commerce.user.api.service.UserAddressService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val cartItemReader: CartItemReader, // 장바구니 조회용 컴포넌트
    private val orderNumberGenerator: OrderNumberGenerator,
    private val productSnapshotReader: ProductSnapshotReader,
    private val userAddressService: UserAddressService,
    private val paymentReader: PaymentReader
) {

    // 배송비 정책 (정책에 따라 변경 예정)
    private fun calculateShippingFee(itemsSubtotal: Int): Int = if (itemsSubtotal >= 20000) 0 else 3000

    @Transactional
    fun prepareOrder(cartItemIds: Set<Long>): PrepareOrderApiResponse {
        // 1. 장바구니 아이템 정보 조회 (CartItemReader를 통해)
        //TODO: 인증된 사용자 ID 값 넘길 수 있도록 수정 필요 (userId <- 이 부분 제거후 수정 필요)
        val userId = 1L
        val cartItems = cartItemReader.readCartItems(userId, cartItemIds)

        // 2. 상품 스냅샷 조회 및 가공
        val items = cartItems.map { cartItem ->
            val snapshot = productSnapshotReader.getById(cartItem.productSnapshotId!!)
            PrepareOrderItemApiResponse(
                productId = snapshot.productId,
                name = snapshot.name,
                thumbnail = snapshot.thumbnail,
                unitPrice = snapshot.price,
                quantity = cartItem.quantity,
                itemSubtotal = snapshot.price * cartItem.quantity
            )
        }

        // 3. 가격 계산
        val itemsSubtotal = items.sumOf { it.itemSubtotal }
        val shippingFee = calculateShippingFee(itemsSubtotal)
        val finalTotalPrice = itemsSubtotal + shippingFee

        // 4. 배송지/결제수단 정보
        val defaultAddress = userAddressService.findDefaultUserAddress(userId)
            ?: throw IllegalStateException("기본 배송지가 없습니다.") // 도메인에 맞는 에러 처리로 변경 가능

        val shippingInfo = PrepareOrderShippingInfoApiResponse(
            recipientName = defaultAddress.recipientName,
            recipientPhone = defaultAddress.recipientPhone,
            zipCode = defaultAddress.zipCode,
            addressId = defaultAddress.addressId,
            address1 = defaultAddress.address1,
            address2 = defaultAddress.address2
        )

        //TODO: 추후 수정 필요 (현재는 주문전 받을 수 있는 값이 없음)
        val paymentMethods = listOf(EnumResponse(PaymentMethod.TOSS_PAY.toString(), PaymentMethod.TOSS_PAY.label))

        // 5. 응답 조립
        return PrepareOrderApiResponse(
            cartItemIds = cartItemIds,
            itemsSubtotal = itemsSubtotal,
            shippingFee = shippingFee,
            finalTotalPrice = finalTotalPrice,
            items = items,
            shippingInfo = shippingInfo,
            paymentMethod = paymentMethods
        )
    }

    @Transactional
    fun createOrder(userId: Long, request: OrderApiRequest): OrderApiResponse {
        // 1. 장바구니 아이템 정보 조회 (상품ID, 수량, 가격)
        val cartItems = cartItemReader.readCartItems(userId, request.cartItemIds)

        // 2. 총 주문 금액 계산
        val totalAmount = cartItems.sumOf { it.unitPrice!! * it.quantity }

        // 난수 생성
        val orderId = 1L

        // 3. 주문 번호 생성
        val orderNumber = orderNumberGenerator.generate(orderId)

        // 4. Order Entity 생성 및 저장
        val order = Order(
            orderNumber = orderNumber,
            userId = userId,
            totalAmount = totalAmount,
            recipientName = request.shippingInfo.recipientName,
            recipientPhone = request.shippingInfo.recipientPhone,
            zipCode = request.shippingInfo.zipCode,
            address1 = request.shippingInfo.address1,
            address2 = request.shippingInfo.address2,
            deliveryMessage = request.shippingInfo.deliveryMessage,
            status = OrderStatus.WAITING_FOR_PAYMENT // 상태 초기화
        )
        orderRepository.save(order)

        // 5. OrderItem Entity 생성 및 저장
        val orderItems = cartItems.map { cartItem ->
            OrderItem(
                orderId = order.id!!,
                productSnapshotId = cartItem.productSnapshotId!!,
                quantity = cartItem.quantity,
                unitPrice = cartItem.unitPrice!!
            )
        }
        orderItemRepository.saveAll(orderItems)

        // 6. 응답 반환
        return OrderApiResponse(orderNumber)
    }

}
