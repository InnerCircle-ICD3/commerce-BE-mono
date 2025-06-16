package com.fastcampus.commerce.admin.order.application

import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderSearchRequest
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderListResponse
import com.fastcampus.commerce.admin.order.interfaces.AdminOrderQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AdminOrderService(
    private val adminOrderQuery: AdminOrderQuery
) {
    fun getOrders(request: AdminOrderSearchRequest, pageable: Pageable): Page<AdminOrderListResponse> {
        return adminOrderQuery.searchOrders(request, pageable, pageable.sort)
    }
}
