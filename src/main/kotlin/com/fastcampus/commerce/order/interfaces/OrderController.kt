package com.fastcampus.commerce.order.interfaces

import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.common.response.EnumResponse
import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.order.application.order.OrderService
import com.fastcampus.commerce.order.interfaces.request.OrderApiRequest
import com.fastcampus.commerce.order.interfaces.request.SearchOrderApiRequest
import com.fastcampus.commerce.order.interfaces.response.CancelOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.GetOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.OrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.PrepareOrderApiResponse
import com.fastcampus.commerce.order.interfaces.response.SearchOrderApiResponse
import com.fastcampus.commerce.user.domain.enums.UserRole
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/orders")
@RestController
class OrderController(
    private val orderService: OrderService,
) {
    @GetMapping("/status")
    fun getOrderStatus(): List<EnumResponse> {
        return orderService.getOrderStatus()
    }

    @GetMapping("/prepare")
    fun prepareOrders(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @RequestParam cartItemIds: String,
    ): PrepareOrderApiResponse {
        val cartItemIdList: Set<Long> = cartItemIds.split(",")
            .map { it.trim().toLong() }
            .toSet()
        return orderService.prepareOrder(user, cartItemIdList)
    }

    @PostMapping
    fun orders(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @RequestBody request: OrderApiRequest,
    ): OrderApiResponse {
        return orderService.createOrder(user, request)
    }

    @GetMapping
    fun getOrders(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @ModelAttribute request: SearchOrderApiRequest,
        pageable: Pageable,
    ): PagedData<SearchOrderApiResponse> {
        return PagedData.of(orderService.getOrders(user, request, pageable))
    }

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
