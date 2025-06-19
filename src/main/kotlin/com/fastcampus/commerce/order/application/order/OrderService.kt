package com.fastcampus.commerce.order.application.order

import com.fastcampus.commerce.cart.application.query.CartItemReader
import com.fastcampus.commerce.cart.infrastructure.repository.CartItemRepository
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.id.UniqueIdGenerator
import com.fastcampus.commerce.common.response.EnumResponse
import com.fastcampus.commerce.common.util.TimeProvider
import com.fastcampus.commerce.order.application.query.ProductSnapshotReader
import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.entity.OrderItem
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.entity.ProductSnapshot
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.order.domain.repository.OrderItemRepository
import com.fastcampus.commerce.order.domain.repository.OrderRepository
import com.fastcampus.commerce.order.infrastructure.OrderQueryRepository
import com.fastcampus.commerce.order.infrastructure.repository.ProductSnapshotRepository
import com.fastcampus.commerce.order.interfaces.request.OrderApiRequest
import com.fastcampus.commerce.order.interfaces.request.SearchOrderApiRequest
import com.fastcampus.commerce.order.interfaces.response.GetOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.GetOrderItemApiResponse
import com.fastcampus.commerce.order.interfaces.response.GetOrderShippingInfoApiResponse
import com.fastcampus.commerce.order.interfaces.response.OrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderItemApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderShippingInfoApiResponse
import com.fastcampus.commerce.order.interfaces.response.SearchOrderApiResponse
import com.fastcampus.commerce.payment.domain.entity.Payment
import com.fastcampus.commerce.payment.domain.entity.PaymentMethod
import com.fastcampus.commerce.payment.domain.error.PaymentErrorCode
import com.fastcampus.commerce.payment.domain.repository.PaymentRepository
import com.fastcampus.commerce.payment.domain.service.PaymentReader
import com.fastcampus.commerce.product.domain.service.ProductReader
import com.fastcampus.commerce.review.domain.repository.ReviewRepository
import com.fastcampus.commerce.user.api.service.UserAddressService
import com.fastcampus.commerce.user.domain.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val cartItemReader: CartItemReader, // 장바구니 조회용 컴포넌트
    private val cartItemRepository: CartItemRepository,
    private val productReader: ProductReader,
    private val productSnapshotReader: ProductSnapshotReader,
    private val userAddressService: UserAddressService,
    private val paymentReader: PaymentReader,
    private val reviewRepository: ReviewRepository,
    private val timeProvider: TimeProvider,
    private val paymentRepository: PaymentRepository,
    private val productSnapshotRepository: ProductSnapshotRepository,
    private val orderQueryRepository: OrderQueryRepository,
) {
    // 배송비 정책 (정책에 따라 변경 예정)
    private fun calculateShippingFee(itemsSubtotal: Int): Int = if (itemsSubtotal >= 30000) 0 else 3000

    fun getOrderStatus(): List<EnumResponse> {
        return OrderStatus.entries.map {
            EnumResponse(it.name, it.label)
        }
    }

    @Transactional
    fun prepareOrder(user: User, cartItemIds: Set<Long>): PrepareOrderApiResponse {
        // 1. 장바구니 아이템 정보 조회 (CartItemReader를 통해)
        val userId = user.id!!
        val cartItems = cartItemReader.readCartItems(userId, cartItemIds)
        val items = cartItems.map { cartItem ->
            val product = productReader.getProductById(cartItem.productId)
            val inventory = productReader.getInventoryByProductId(cartItem.productId)
            if (cartItem.quantity > inventory.quantity) {
                throw CoreException(
                    OrderErrorCode.ORDER_QUANTITY_NOT_ENOUGH,
                    "${product.name}: 남은 수량: ${inventory.quantity}. 요청 수량: ${cartItem.quantity}",
                )
            }
            PrepareOrderItemApiResponse.of(cartItem, product)
        }

        // 3. 가격 계산
        val itemsSubtotal = items.sumOf { it.itemSubtotal }
        val shippingFee = calculateShippingFee(itemsSubtotal)
        val finalTotalPrice = itemsSubtotal + shippingFee

        // 4. 배송지/결제수단 정보
        val defaultAddress = userAddressService.findDefaultUserAddress(userId)
        val shippingInfo = if (defaultAddress == null) {
            null
        } else {
            PrepareOrderShippingInfoApiResponse(
                recipientName = defaultAddress.recipientName,
                recipientPhone = defaultAddress.recipientPhone,
                zipCode = defaultAddress.zipCode,
                addressId = defaultAddress.addressId,
                address1 = defaultAddress.address1,
                address2 = defaultAddress.address2,
            )
        }

        // TODO: 추후 수정 필요 (현재는 주문전 받을 수 있는 값이 없음)
        val paymentMethods = listOf(EnumResponse(PaymentMethod.TOSS_PAY.toString(), PaymentMethod.TOSS_PAY.label))

        // 5. 응답 조립
        return PrepareOrderApiResponse(
            cartItemIds = cartItemIds,
            itemsSubtotal = itemsSubtotal,
            shippingFee = shippingFee,
            finalTotalPrice = finalTotalPrice,
            items = items,
            shippingInfo = shippingInfo,
            paymentMethod = paymentMethods,
        )
    }

    @Transactional
    fun createOrder(user: User, request: OrderApiRequest): OrderApiResponse {
        val userId = user.id!!

        // 1. 장바구니 아이템 정보 조회 (상품ID, 수량, 가격)
        val cartItems = cartItemReader.readCartItems(userId, request.cartItemIds)
        val items = cartItems.map { cartItem ->
            val product = productReader.getProductById(cartItem.productId)
            val inventory = productReader.getInventoryByProductId(cartItem.productId)

            if (cartItem.quantity > inventory.quantity) {
                throw CoreException(
                    OrderErrorCode.ORDER_QUANTITY_NOT_ENOUGH,
                    "${product.name}: 남은 수량: ${inventory.quantity}. 요청 수량: ${cartItem.quantity}",
                )
            }
            PrepareOrderItemApiResponse.of(cartItem, product)
        }

        // 3. 주문 번호 생성
        val now = timeProvider.now().toLocalDate()
        val orderNumber = UniqueIdGenerator.generateOrderNumber(now)

        // 4. Order Entity 생성 및 저장
        val itemsSubtotal = items.sumOf { it.itemSubtotal }
        val order = Order(
            orderNumber = orderNumber,
            userId = userId,
            totalAmount = itemsSubtotal + calculateShippingFee(itemsSubtotal),
            recipientName = request.shippingInfo.recipientName,
            recipientPhone = request.shippingInfo.recipientPhone,
            zipCode = request.shippingInfo.zipCode,
            address1 = request.shippingInfo.address1,
            address2 = request.shippingInfo.address2,
            deliveryMessage = request.shippingInfo.deliveryMessage,
        )
        orderRepository.save(order)

        // 5. OrderItem Entity 생성 및 저장
        val orderItems = items.map { cartItem ->
            var snapshot = productSnapshotReader.findLatestByProductId(cartItem.productId)
            if (
                snapshot == null ||
                snapshot.name != cartItem.name ||
                snapshot.price != cartItem.unitPrice ||
                snapshot.thumbnail != cartItem.thumbnail
            ) {
                snapshot = productSnapshotRepository.save(
                    ProductSnapshot(
                        productId = cartItem.productId,
                        name = cartItem.name,
                        price = cartItem.unitPrice,
                        thumbnail = cartItem.thumbnail,
                    ),
                )
            }

            OrderItem(
                orderId = order.id!!,
                productSnapshotId = snapshot.id!!,
                quantity = cartItem.quantity,
                unitPrice = cartItem.unitPrice,
            )
        }
        orderItemRepository.saveAll(orderItems)

        val paymentMethod = (
            PaymentMethod.fromCode(request.paymentMethod)
                ?: throw CoreException(PaymentErrorCode.INVALID_PAYMENT_METHOD)
        )

        paymentRepository.save(
            Payment(
                paymentNumber = UniqueIdGenerator.generatePaymentNumber(now),
                orderId = order.id!!,
                userId = userId,
                amount = order.totalAmount,
                paymentMethod = paymentMethod,
            ),
        )
        cartItemRepository.softDeleteByIds(cartItems.map { it.cartItemId })

        // 6. 응답 반환
        return OrderApiResponse(orderNumber)
    }

    @Transactional(readOnly = true)
    fun getOrders(user: User, request: SearchOrderApiRequest, pageable: Pageable): Page<SearchOrderApiResponse> {
        // 1. 조건에 맞는 주문 페이지 조회 (ex: 유저ID 기준)
        val orders = orderQueryRepository.getUserOrders(request.toCondition(user.id!!, timeProvider.now()), pageable)

        // 2. 주문별 대표 상품, 가격, 썸네일 등 요약 정보 가공
        val responses = orders.map { order ->
            val orderItems = orderItemRepository.findByOrderId(order.id!!)
            val productSnapshot = productSnapshotReader.getById(orderItems.first().productSnapshotId)
            val orderName = let { "${productSnapshot.name} 외 ${orderItems.size - 1}건" } ?: "주문상품 없음"
            val mainProductThumbnail = productSnapshot.thumbnail

            SearchOrderApiResponse(
                orderNumber = order.orderNumber,
                orderName = orderName,
                mainProductThumbnail = mainProductThumbnail,
                orderStatus = order.status,
                finalTotalPrice = order.totalAmount,
                orderedAt = order.createdAt,
                trackingNumber = order.trackingNumber,
                cancellable = order.status.isCancellable(),
                refundable = order.status.isRefundable(),
            )
        }
        return responses
    }

    @Transactional(readOnly = true)
    fun getOrderDetail(orderNumber: String): GetOrderApiResponse {
        // 1. 주문 정보 조회
        val order = orderRepository.findByOrderNumber(orderNumber)
            ?: throw CoreException(OrderErrorCode.ORDER_NOT_FOUND)

        // 2. 주문 상품 목록 조회
        val orderItems = orderItemRepository.findByOrderId(order.id!!)
        val itemResponses = orderItems.map {
            val productSnapshot = productSnapshotReader.getById(it.productSnapshotId)
            GetOrderItemApiResponse(
                orderItemId = it.id!!,
                productSnapshotId = it.productSnapshotId,
                name = productSnapshot.name,
                thumbnail = productSnapshot.thumbnail,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
                itemSubTotal = it.unitPrice * it.quantity,
            )
        }

        // 2-1. 리뷰 존재 여부 조회
        val hasReview = orderItems.any { reviewRepository.existsByUserIdAndOrderItemId(order.userId, it.id!!) }

        // 3. 배송지 정보 (Order 엔티티에 직접 포함되어 있다고 가정)
        val shippingInfo = GetOrderShippingInfoApiResponse(
            recipientName = order.recipientName,
            recipientPhone = order.recipientPhone,
            zipCode = order.zipCode,
            address1 = order.address1,
            address2 = order.address2,
            deliveryMessage = order.deliveryMessage,
        )

        // 4. 결제 정보 조회 (Payment와 1:1 매핑된다고 가정)
        val payment = paymentReader.getByOrderId(order.id!!)
        val paymentMethodLabel = payment.paymentMethod.label
        val paymentNumber = payment.paymentNumber
        val paidAt = payment.createdAt

        // 5. 주문 상태 등 Boolean 가공
        val cancellable = order.status.isCancellable()
        val refundable = order.status.isCancellable()

        val shippingFee = calculateShippingFee(order.totalAmount)

        // 6. 응답 조립
        return GetOrderApiResponse(
            orderNumber = order.orderNumber,
            orderName = order.recipientName + "님의 주문", // 실무에서는 별도 가공 필요
            orderStatus = order.status.name, // 예: "배송 준비중"
            trackingNumber = order.trackingNumber,
            paymentNumber = paymentNumber,
            paymentMethod = paymentMethodLabel,
            itemsSubTotal = order.totalAmount - shippingFee, // 필요에 따라 계산
            shippingFee = shippingFee,
            finalTotalPrice = order.totalAmount,
            items = itemResponses,
            shippingInfo = shippingInfo,
            orderedAt = order.createdAt,
            paidAt = paidAt,
            cancellable = cancellable,
            cancelRequested = !cancellable,
            cancelledAt = order.cancelledAt,
            refundable = refundable,
            refundRequested = !cancellable,
            refundRequestedAt = order.refundRequestedAt,
            refunded = order.refundRequestedAt != null,
            refundedAt = order.refundedAt,
            reviewable = hasReview,
            reviewWritten = hasReview,
        )
    }

    @Transactional
    fun cancelOrder(orderNumber: String) {
        val order = orderRepository.findByOrderNumber(orderNumber)
            ?: throw CoreException(OrderErrorCode.ORDER_NOT_FOUND)
        val cancelledAt = timeProvider.now()
        order.cancel(cancelledAt)
    }
}
