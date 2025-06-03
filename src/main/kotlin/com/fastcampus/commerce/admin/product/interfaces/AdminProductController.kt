package com.fastcampus.commerce.admin.product.interfaces

import com.fastcampus.commerce.admin.product.application.AdminProductService
import com.fastcampus.commerce.admin.product.interfaces.request.RegisterProductApiRequest
import com.fastcampus.commerce.admin.product.interfaces.response.RegisterProductApiResponse
import com.fastcampus.commerce.common.response.EnumResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// TODO: 관리자 인증/인가를 위한 Annotation 추가 - Auth와 연동
@RequestMapping("/admin/products")
@RestController
class AdminProductController(
    private val adminProductService: AdminProductService,
) {
    @GetMapping("/selling-status")
    fun getSellingStatus(): List<EnumResponse> {
        val sellingStatuses = adminProductService.getSellingStatus()
        return sellingStatuses.map { EnumResponse(it.code, it.label) }
    }

    @PostMapping
    fun registerProduct(
        @RequestBody request: RegisterProductApiRequest,
    ): RegisterProductApiResponse {
        val adminId = 1L
        val productId: Long = adminProductService.register(adminId, request.toServiceRequest())
        return RegisterProductApiResponse(productId)
    }
}
