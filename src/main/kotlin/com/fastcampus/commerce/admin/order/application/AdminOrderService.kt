package com.fastcampus.commerce.admin.order.application

import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderCreateRequest
import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderSearchRequest
import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderUpdateRequest
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderCreateResponse
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderDetailItemResponse
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderDetailResponse
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderListResponse
import com.fastcampus.commerce.admin.order.interfaces.AdminOrderQuery
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.order.application.query.ProductSnapshotReader
import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderItem
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.order.domain.repository.OrderItemRepository
import com.fastcampus.commerce.order.domain.repository.OrderRepository
import com.fastcampus.commerce.order.domain.service.OrderNumberGenerator
import com.fastcampus.commerce.order.infrastructure.repository.ProductSnapshotRepository
import com.fastcampus.commerce.payment.domain.service.PaymentReader
import com.fastcampus.commerce.user.domain.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminOrderService(
    private val adminOrderQuery: AdminOrderQuery,
    private val productSnapshotReader: ProductSnapshotReader,
    private val orderNumberGenerator: OrderNumberGenerator,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val userRepository: UserRepository,
    private val productSnapshotRepository: ProductSnapshotRepository,
    private val paymentReader: PaymentReader,
    private val timeProvider: TimeProvider,
) {
    // 주문 조회
    fun getOrders(request: AdminOrderSearchRequest, pageable: Pageable): Page<AdminOrderListResponse> {
        return adminOrderQuery.searchOrders(request, pageable, pageable.sort)
    }

    // 주문 상세 조회
    @Transactional(readOnly = true)
    fun getOrderDetail(orderId: Long): AdminOrderDetailResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { CoreException(OrderErrorCode.ORDER_NOT_FOUND) }
        val user = userRepository.findById(order.userId)
            .orElse(null)
        val payment = paymentReader.getByOrderId(order.id!!)

        val items = orderItemRepository.findByOrderId(order.id!!).map { orderItem ->
            val productSnapshot = productSnapshotRepository.findById(orderItem.productSnapshotId!!).get()
            AdminOrderDetailItemResponse(
                productName = productSnapshot.name,
                quantity = orderItem.quantity,
                price = orderItem.unitPrice,
                total = orderItem.unitPrice * orderItem.quantity,
                thumbnail = productSnapshot.thumbnail,
            )
        }

        val subtotal = items.sumOf { it.total }

        return AdminOrderDetailResponse(
            orderNumber = order.orderNumber,
            status = order.status.name,
            trackingNumber = order.trackingNumber,
            createdAt = order.createdAt,
            paymentMethod = payment.paymentMethod.label,
            address = listOfNotNull(order.address1, order.address2).joinToString(" "),
            recipientName = order.recipientName,
            recipientPhone = order.recipientPhone,
            customerName = user?.name ?: "",
            customerEmail = user?.email ?: "",
            items = items,
            subtotal = subtotal,
            total = order.totalAmount,
        )
    }

    // 주문 생성
    @Transactional
    fun createOrder(request: AdminOrderCreateRequest): AdminOrderCreateResponse {
        // 1. 주문상품 정보/가격 검증
        val orderItems = request.orderItems.map { itemReq ->
            val productSnapshot = productSnapshotReader.getById(itemReq.productSnapshotId)
            OrderItem(
                orderId = 0L, // 나중에 갱신
                productSnapshotId = itemReq.productSnapshotId,
                quantity = itemReq.quantity,
                unitPrice = productSnapshot.price,
            )
        }
        val totalAmount = orderItems.sumOf { it.unitPrice * it.quantity }

        // 2. 주문 엔티티 생성/저장
        val order = Order(
            orderNumber = orderNumberGenerator.generate(1L),
            userId = request.userId,
            totalAmount = totalAmount,
            recipientName = request.recipientName,
            recipientPhone = request.recipientPhone,
            zipCode = request.zipCode,
            address1 = request.address1,
            address2 = request.address2,
            deliveryMessage = request.deliveryMessage,
            status = OrderStatus.WAITING_FOR_PAYMENT,
        )
        orderRepository.save(order)

        // 3. 주문상품(OrderItem) 저장
        orderItems.forEach { it.orderId = order.id!! }
        orderItemRepository.saveAll(orderItems)

        // 4. 응답 반환
        return AdminOrderCreateResponse(
            orderId = order.id!!,
            orderNumber = order.orderNumber,
            totalAmount = order.totalAmount,
            orderStatus = order.status.name,
            orderedAt = order.createdAt,
        )
    }

    // 주문 취소
    @Transactional
    fun cancelOrder(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { CoreException(OrderErrorCode.ORDER_NOT_FOUND) }

        // 비즈니스 정책: 이미 배송된 주문은 취소 불가 등
        if (!order.status.isCancellable()) {
            throw CoreException(OrderErrorCode.ORDER_CANNOT_BE_CANCELLED)
        }

        order.cancel(cancelledAt = LocalDateTime.now())
    }

    // 주문 수정
    @Transactional
    fun updateOrder(orderId: Long, request: AdminOrderUpdateRequest) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { CoreException(OrderErrorCode.ORDER_NOT_FOUND) }

        // 필드별로 변경 요청이 있으면 값 변경
        request.recipientName?.let { order.recipientName = it }
        request.recipientPhone?.let { order.recipientPhone = it }
        request.zipCode?.let { order.zipCode = it }
        request.address1?.let { order.address1 = it }
        request.address2?.let { order.address2 = it }
        request.deliveryMessage?.let { order.deliveryMessage = it }

        // 업데이트는 엔티티 dirty checking + @Transactional로 자동 반영
    }

    @Transactional
    fun preparingShipment(orderId: Long, trackingNumber: String) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { CoreException(OrderErrorCode.ORDER_NOT_FOUND) }

        val preparingShipmentAt = timeProvider.now()
        order.preparingShipment(trackingNumber,preparingShipmentAt)
    }

    @Transactional
    fun shippedOrder(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { CoreException(OrderErrorCode.ORDER_NOT_FOUND) }

        val shippedAt = timeProvider.now()
        order.shipping(shippedAt)
    }

    @Transactional
    fun deliveredOrder(orderId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { CoreException(OrderErrorCode.ORDER_NOT_FOUND) }

        val deliveredAt = timeProvider.now()
        order.delivered(deliveredAt)
    }
}
