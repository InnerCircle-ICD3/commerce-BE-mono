package com.fastcampus.commerce.order.interfaces

import com.fastcampus.commerce.auth.interfaces.web.security.annotation.WithRoles
import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.order.application.order.OrderService
import com.fastcampus.commerce.order.interfaces.request.OrderApiRequest
import com.fastcampus.commerce.order.interfaces.request.SearchOrderApiRequest
import com.fastcampus.commerce.order.interfaces.response.CancelOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.GetOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.GetOrderItemApiResponse
import com.fastcampus.commerce.order.interfaces.response.GetOrderShippingInfoApiResponse
import com.fastcampus.commerce.order.interfaces.response.OrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.SearchOrderApiResponse
import com.fastcampus.commerce.user.domain.entity.User
import com.fastcampus.commerce.user.domain.enums.UserRole
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RequestMapping("/orders")
@RestController
class OrderController(
    private val orderService: OrderService
) {
    /*@GetMapping("/prepare")
    fun prepareOrders(
        @RequestParam cartItemIds: String,
    ): PrepareOrderApiResponse {
        return PrepareOrderApiResponse(
            cartItemIds = listOf(1L),
            itemsSubtotal = 10000,
            shippingFee = 3000,
            finalTotalPrice = 13000,
            items = listOf(
                PrepareOrderItemApiResponse(
                    productId = 1L,
                    name = "상품A",
                    thumbnail = "http://localhost:8080/api/v1/product/1/thumbnail",
                    unitPrice = 1000,
                    quantity = 10,
                    itemSubtotal = 10000,
                ),
            ),
            shippingInfo = PrepareOrderShippingInfoApiResponse(
                recipientName = "홍길동",
                recipientPhone = "010-1234-1234",
                zipCode = "12345",
                addressId = 1L,
                address1 = "서울특별시 관악구",
                address2 = "서울대입구역 6번출구",
            ),
            paymentMethod = listOf(
                EnumResponse("MOCK", "테스트"),
            ),
        )
    }*/
    @GetMapping("/prepare")
    fun prepareOrders(
        @WithRoles([UserRole.USER]) user: User, @RequestParam cartItemIds: String,
    ): PrepareOrderApiResponse {
        // cartItemIds: "1,2,3" 형태라고 가정
        val cartItemIdList: Set<Long> = cartItemIds.split(",")
            .map { it.trim().toLong() }
            .toSet()
        return orderService.prepareOrder(user, cartItemIdList)
    }

    @PostMapping
    fun orders(
        @WithRoles([UserRole.USER]) user: User, @RequestBody request: OrderApiRequest,
    ): OrderApiResponse {
        //TODO: 인증된 사용자 ID 값 넘길 수 있도록 수정 필요 (request.userId <- 이 부분 제거후 수정 필요)
        return orderService.createOrder(user, request)
    }

    /*@GetMapping
    fun getOrders(
        @ModelAttribute request: SearchOrderApiRequest,
        @PageableDefault(page = 1, size = 10) pageable: Pageable,
    ): PagedData<SearchOrderApiResponse> {
        val searchOrderApiResponse = SearchOrderApiResponse(
            orderNumber = "ORD20250609123456789",
            orderName = "스페셜 리버즈 외 3건",
            mainProductThumbnail = "https://example.com/thumbnail.jpg",
            orderStatus = "배송 준비중",
            finalTotalPrice = 13000,
            orderedAt = LocalDateTime.of(2025, 6, 8, 12, 34),
            cancellable = true,
            refundable = false,
        )
        val page = PageImpl(listOf(searchOrderApiResponse), PageRequest.of(1, 10), 1L)
        return PagedData.of(page)
    }*/

    @GetMapping
    fun getOrders(
        @ModelAttribute request: SearchOrderApiRequest,
        @PageableDefault(page = 1, size = 10) pageable: Pageable,
    ): PagedData<SearchOrderApiResponse> {
        return PagedData.of(orderService.getOrders(request, pageable))
    }

    /*@GetMapping("/{orderNumber}")
    fun getOrders(
        @PathVariable orderNumber: String,
    ): GetOrderApiResponse {
        val orderedAt = LocalDateTime.of(2025, 6, 8, 12, 34)
        return GetOrderApiResponse(
            orderNumber = orderNumber,
            orderName = "홍길동님의 주문",
            orderStatus = "배송 준비중",
            paymentNumber = "PAY1231414124",
            paymentMethod = "토스페이",
            itemsSubTotal = 10000,
            shippingFee = 3000,
            finalTotalPrice = 13000,
            items = listOf(
                GetOrderItemApiResponse(
                    orderItemId = 1L,
                    productSnapshotId = 1L,
                    name = "상품명",
                    thumbnail = "https://example.com/thumbnail.jpg",
                    unitPrice = 1000,
                    quantity = 10,
                    itemSubTotal = 10000,
                ),
            ),
            shippingInfo = GetOrderShippingInfoApiResponse(
                recipientName = "홍길동",
                recipientPhone = "010-1234-1234",
                zipCode = "08123",
                address1 = "서울특별시 관악구",
                address2 = "1000003동 123호",
                deliveryMessage = "문앞에 놔주세요.",
            ),
            orderedAt = orderedAt,
            paidAt = orderedAt,
            cancellable = true,
            cancelRequested = false,
            cancelledAt = null,
            refundable = false,
            refundRequested = false,
            refundRequestedAt = null,
            refunded = false,
            refundedAt = null,
            reviewable = true,
            reviewWritten = false,
        )
    }*/

    @GetMapping("/{orderNumber}")
    fun getOrders(
        @PathVariable orderNumber: String,
    ): GetOrderApiResponse {
        return orderService.getOrderDetail(orderNumber)
    }

    @PostMapping("/{orderNumber}/cancel")
    fun cancelOrder(
        @PathVariable orderNumber: String,
    ): CancelOrderApiResponse {
        orderService.cancelOrder(orderNumber)
       return CancelOrderApiResponse()
    }
}
