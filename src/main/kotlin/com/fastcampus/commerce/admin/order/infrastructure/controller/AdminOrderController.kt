package com.fastcampus.commerce.admin.order.infrastructure.controller

import com.fastcampus.commerce.admin.order.application.AdminOrderService
import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderCreateRequest
import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderSearchRequest
import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderUpdateRequest
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderCreateResponse
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderDetailResponse
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderListResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/orders")
class AdminOrderController(
    private val adminOrderService: AdminOrderService
) {
    // 주문 조회
    @GetMapping
    fun getOrders(
        @ModelAttribute search: AdminOrderSearchRequest,
        @PageableDefault(size = 20, sort = ["orderDate"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<AdminOrderListResponse> {
        return adminOrderService.getOrders(search, pageable)
    }

    //주문 상세 조회
    @GetMapping("/{orderId}")
    fun getOrderDetail(@PathVariable orderId: Long): AdminOrderDetailResponse {
        return adminOrderService.getOrderDetail(orderId)
    }

    // 주문 생성
    @PostMapping
    fun createOrder(@RequestBody request: AdminOrderCreateRequest): AdminOrderCreateResponse {
        return adminOrderService.createOrder(request)
    }

    //주문 취소
    @DeleteMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: Long) {
        adminOrderService.cancelOrder(orderId)
    }

    //주문 수정
    @PatchMapping("/{orderId}")
    fun updateOrder(
        @PathVariable orderId: Long,
        @RequestBody request: AdminOrderUpdateRequest
    ) {
        adminOrderService.updateOrder(orderId, request)
    }
}
