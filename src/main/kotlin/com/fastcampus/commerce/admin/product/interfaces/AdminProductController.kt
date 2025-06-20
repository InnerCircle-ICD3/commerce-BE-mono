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
import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.common.response.EnumResponse
import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.user.domain.enums.UserRole
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
import jakarta.validation.Valid

@RequestMapping("/admin/products")
@RestController
class AdminProductController(
    private val adminProductService: AdminProductService,
) {
    @GetMapping("/selling-status")
    fun getSellingStatus(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
    ): List<EnumResponse> {
        val sellingStatuses = adminProductService.getSellingStatus()
        return sellingStatuses.map { EnumResponse(it.code, it.label) }
    }

    @GetMapping
    fun searchProducts(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @ModelAttribute request: SearchAdminProductApiRequest,
        pageable: Pageable,
    ): PagedData<SearchAdminProductApiResponse> {
        val products = adminProductService.searchProducts(request.toServiceRequest(), pageable)
        return PagedData.of(products.map(SearchAdminProductApiResponse::from))
    }

    @GetMapping("/{productId}")
    fun getProduct(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable productId: Long,
    ): AdminProductDetailApiResponse {
        return AdminProductDetailApiResponse.from(adminProductService.getProduct(productId))
    }

    @PostMapping
    fun registerProduct(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @Valid @RequestBody request: RegisterProductApiRequest,
    ): RegisterProductApiResponse {
        val productId: Long = adminProductService.register(admin.id, request.toServiceRequest())
        return RegisterProductApiResponse(productId)
    }

    @PutMapping("/{productId}")
    fun updateProduct(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable productId: Long,
        @Valid @RequestBody request: UpdateProductApiRequest,
    ): UpdateProductApiResponse {
        adminProductService.update(admin.id, request.toServiceRequest(productId))
        return UpdateProductApiResponse(productId)
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable productId: Long,
    ): DeleteProductApiResponse {
        adminProductService.delete(admin.id, productId)
        return DeleteProductApiResponse()
    }
}
