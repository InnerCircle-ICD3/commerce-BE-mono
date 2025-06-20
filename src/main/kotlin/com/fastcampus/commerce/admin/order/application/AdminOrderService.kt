package com.fastcampus.commerce.admin.order.application

import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderSearchRequest
import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderUpdateRequest
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderDetailItemResponse
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderDetailResponse
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderDetailShippingInfoResponse
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderListResponse
import com.fastcampus.commerce.admin.order.interfaces.AdminOrderQueryRepository
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.order.application.query.ProductSnapshotReader
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.order.domain.repository.OrderItemRepository
import com.fastcampus.commerce.order.domain.repository.OrderRepository
import com.fastcampus.commerce.order.infrastructure.repository.ProductSnapshotRepository
import com.fastcampus.commerce.payment.domain.service.PaymentReader
import com.fastcampus.commerce.user.api.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AdminOrderService(
    private val adminOrderQueryRepository: AdminOrderQueryRepository,
    private val productSnapshotReader: ProductSnapshotReader,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val userService: UserService,
    private val productSnapshotRepository: ProductSnapshotRepository,
    private val paymentReader: PaymentReader,
    private val timeProvider: TimeProvider,
) {
    @Transactional(readOnly = true)
    fun getOrders(request: AdminOrderSearchRequest, pageable: Pageable): Page<AdminOrderListResponse> {
        val orders = adminOrderQueryRepository.searchOrders(request, pageable)
        val responses = orders.map { order ->
            val orderItems = orderItemRepository.findByOrderId(order.id!!)
            val productSnapshot = productSnapshotReader.getById(orderItems.first().productSnapshotId)
            val orderName = let { "${productSnapshot.name} 외 ${orderItems.size - 1}건" }
            val user = userService.getUser(order.userId)

            AdminOrderListResponse(
                orderId = order.id!!,
                orderNumber = order.orderNumber,
                orderName = orderName,
                orderStatus = order.status.label,
                finalTotalPrice = order.totalAmount,
                orderedAt = order.createdAt,
                trackingNumber = order.trackingNumber,
                customerId = order.userId,
                customerName = user.nickname,
            )
        }
        return responses
    }

    @Transactional(readOnly = true)
    fun getOrderDetail(orderId: Long): AdminOrderDetailResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { CoreException(OrderErrorCode.ORDER_NOT_FOUND) }
        val payment = paymentReader.getByOrderId(orderId)

        val items = orderItemRepository.findByOrderId(order.id!!).map { orderItem ->
            val productSnapshot = productSnapshotRepository.findById(orderItem.productSnapshotId!!).get()
            AdminOrderDetailItemResponse(
                orderItemId = orderItem.id!!,
                productId = productSnapshot.productId,
                name = productSnapshot.name,
                thumbnail = productSnapshot.thumbnail,
                unitPrice = productSnapshot.price,
                quantity = orderItem.quantity,
                itemSubTotal = orderItem.unitPrice * orderItem.quantity,
            )
        }

        val subtotal = items.sumOf { it.itemSubTotal }

        return AdminOrderDetailResponse(
            orderId = orderId,
            orderNumber = order.orderNumber,
            orderStatus = order.status.label,
            trackingNumber = order.trackingNumber,
            paymentNumber = payment.paymentNumber,
            paymentMethod = payment.paymentMethod.label,
            itemsSubTotal = subtotal,
            shippingFee = order.totalAmount - subtotal,
            finalTotalPrice = order.totalAmount,
            items = items,
            shippingInfo = AdminOrderDetailShippingInfoResponse(
                recipientName = order.recipientName,
                recipientPhone = order.recipientPhone,
                zipCode = order.zipCode,
                address1 = order.address1,
                address2 = order.address2,
                deliveryMessage = order.deliveryMessage,
            ),
            orderedAt = order.createdAt,
            paidAt = order.paidAt,
            cancellable = order.status.isCancellable(),
            cancelRequested = order.cancelledAt != null,
            cancelledAt = order.cancelledAt,
            refundable = order.status.isRefundable(),
            refundRequested = order.refundRequestedAt != null,
            refundRequestedAt = order.refundRequestedAt,
            refunded = order.refundedAt != null,
            refundedAt = order.refundedAt,
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
        order.preparingShipment(trackingNumber, preparingShipmentAt)
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
