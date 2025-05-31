package com.fastcampus.commerce.admin.product.interfaces

import com.fastcampus.commerce.admin.product.domain.error.AdminProductErrorCode
import com.fastcampus.commerce.admin.product.interfaces.dto.RegisterProductRequest
import com.fastcampus.commerce.admin.product.interfaces.dto.RegisterProductResponse
import com.fastcampus.commerce.admin.product.interfaces.dto.UpdateProductRequest
import com.fastcampus.commerce.admin.product.interfaces.dto.UpdateProductResponse
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.response.EnumResponse
import com.fastcampus.commerce.product.interfaces.dto.ProductSellingStatusResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/admin/products")
@RestController
class AdminProductController {
    @GetMapping("/selling-status")
    fun getSellingStatus(): ProductSellingStatusResponse {
        return ProductSellingStatusResponse(
            status = listOf(
                EnumResponse("ON_SALE", "판매중"),
                EnumResponse("UNAVAILABLE", "판매중지"),
                EnumResponse("HIDDEN", "숨김"),
            ),
        )
    }

    @PostMapping
    fun registerProduct(
        @RequestBody request: RegisterProductRequest,
    ): RegisterProductResponse {
        if (request.name.isNullOrBlank()) {
            throw CoreException(AdminProductErrorCode.PRODUCT_NAME_EMPTY)
        }
        return RegisterProductResponse(1)
    }

    @PutMapping("/{productId}")
    fun updateProduct(
        @PathVariable productId: Long,
        @RequestBody request: UpdateProductRequest,
    ): UpdateProductResponse {
        return UpdateProductResponse(productId)
    }
}
