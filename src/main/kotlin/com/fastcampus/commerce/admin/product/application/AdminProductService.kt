package com.fastcampus.commerce.admin.product.application

import com.fastcampus.commerce.admin.product.application.response.SellingStatusResponse
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import org.springframework.stereotype.Service

@Service
class AdminProductService {
    fun getSellingStatus(): List<SellingStatusResponse> {
        return SellingStatus.entries
            .map(SellingStatusResponse::from)
    }
}
