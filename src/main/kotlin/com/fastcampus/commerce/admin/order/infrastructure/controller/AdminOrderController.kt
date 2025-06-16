package com.fastcampus.commerce.admin.order.infrastructure.controller

import com.fastcampus.commerce.admin.order.application.AdminOrderService
import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderSearchRequest
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderListResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/orders")
class AdminOrderController(
    private val adminOrderService: AdminOrderService
) {
    @GetMapping
    fun getOrders(
        @ModelAttribute search: AdminOrderSearchRequest,
        @PageableDefault(size = 20, sort = ["orderDate"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<AdminOrderListResponse> {
        return adminOrderService.getOrders(search, pageable)
    }
}
