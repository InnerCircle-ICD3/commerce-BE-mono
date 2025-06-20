package com.fastcampus.commerce.admin.order.interfaces

import com.fastcampus.commerce.admin.order.application.AdminOrderService
import com.fastcampus.commerce.admin.order.interfaces.request.AdminOrderSearchRequest
import com.fastcampus.commerce.admin.order.interfaces.request.PreparingShipmentRequest
import com.fastcampus.commerce.admin.order.interfaces.response.AdminOrderDetailResponse
import com.fastcampus.commerce.admin.order.interfaces.response.AdminOrderListResponse
import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.user.domain.enums.UserRole
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
@RequestMapping("/admin/orders")
class AdminOrderController(
    private val adminOrderService: AdminOrderService,
) {
    @GetMapping
    fun getOrders(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @Valid @ModelAttribute search: AdminOrderSearchRequest,
        pageable: Pageable,
    ): PagedData<AdminOrderListResponse> {
        return PagedData.of(adminOrderService.getOrders(search, pageable))
    }

    @GetMapping("/{orderId}")
    fun getOrderDetail(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable orderId: Long,
    ): AdminOrderDetailResponse {
        return adminOrderService.getOrderDetail(orderId)
    }

    @DeleteMapping("/{orderId}/cancel")
    fun cancelOrder(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable orderId: Long,
    ) {
        adminOrderService.cancelOrder(orderId)
    }

    @PatchMapping("/{orderId}/status/preparing-shipment")
    fun preparingShipmentOrder(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable orderId: Long,
        @Valid @RequestBody request: PreparingShipmentRequest,
    ) {
        adminOrderService.preparingShipment(orderId, request.trackingNumber)
    }

    @PatchMapping("/{orderId}/status/shipped")
    fun shippedOrder(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable orderId: Long,
    ) {
        adminOrderService.shippedOrder(orderId)
    }

    @PatchMapping("/{orderId}/status/delivered")
    fun deliveredOrder(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable orderId: Long,
    ) {
        adminOrderService.deliveredOrder(orderId)
    }
}
