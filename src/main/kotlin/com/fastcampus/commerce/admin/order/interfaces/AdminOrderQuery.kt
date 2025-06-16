package com.fastcampus.commerce.admin.order.interfaces

import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderSearchRequest
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderListResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface AdminOrderQuery {
    fun searchOrders(
        search: AdminOrderSearchRequest,
        pageable: Pageable,
        sort: Sort
    ): Page<AdminOrderListResponse>
}
