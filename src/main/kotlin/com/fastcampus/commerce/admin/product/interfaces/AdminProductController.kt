package com.fastcampus.commerce.admin.product.interfaces

import com.fastcampus.commerce.admin.product.application.AdminProductService
import com.fastcampus.commerce.admin.product.interfaces.request.RegisterProductApiRequest
import com.fastcampus.commerce.admin.product.interfaces.request.SearchAdminProductApiRequest
import com.fastcampus.commerce.admin.product.interfaces.request.UpdateProductApiRequest
import com.fastcampus.commerce.admin.product.interfaces.response.AdminProductDetailApiResponse
import com.fastcampus.commerce.admin.product.interfaces.response.DeleteProductApiResponse
import com.fastcampus.commerce.admin.product.interfaces.response.RegisterProductApiResponse
import com.fastcampus.commerce.admin.product.interfaces.response.SearchAdminProductApiResponse
import com.fastcampus.commerce.admin.product.interfaces.response.UpdateProductApiResponse
import com.fastcampus.commerce.common.response.EnumResponse
import com.fastcampus.commerce.common.response.PagedData
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @GetMapping
    fun searchProducts(
        @ModelAttribute request: SearchAdminProductApiRequest,
        pageable: Pageable,
    ): PagedData<SearchAdminProductApiResponse> {
        val products = adminProductService.searchProducts(request.toServiceRequest(), pageable)
        return PagedData.of(products.map(SearchAdminProductApiResponse::from))
    }

    @GetMapping("/{productId}")
    fun getProduct(
        @PathVariable productId: Long,
    ): AdminProductDetailApiResponse {
        return AdminProductDetailApiResponse.from(adminProductService.getProduct(productId))
    }

    @PostMapping
    fun registerProduct(
        @RequestBody request: RegisterProductApiRequest,
    ): RegisterProductApiResponse {
        val adminId = 1L
        val productId: Long = adminProductService.register(adminId, request.toServiceRequest())
        return RegisterProductApiResponse(productId)
    }

    @PutMapping("/{productId}")
    fun updateProduct(
        @PathVariable productId: Long,
        @RequestBody request: UpdateProductApiRequest,
    ): UpdateProductApiResponse {
        val adminId = 1L
        adminProductService.update(adminId, request.toServiceRequest(productId))
        return UpdateProductApiResponse(productId)
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(
        @PathVariable productId: Long,
    ): DeleteProductApiResponse {
        val adminId = 1L
        adminProductService.delete(adminId, productId)
        return DeleteProductApiResponse()
    }
}
