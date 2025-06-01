package com.fastcampus.commerce.admin.product.interfaces

import com.fastcampus.commerce.admin.product.domain.error.AdminProductErrorCode
import com.fastcampus.commerce.admin.product.interfaces.dto.DeleteProductResponse
import com.fastcampus.commerce.admin.product.interfaces.dto.ProductResponse
import com.fastcampus.commerce.admin.product.interfaces.dto.RegisterProductRequest
import com.fastcampus.commerce.admin.product.interfaces.dto.RegisterProductResponse
import com.fastcampus.commerce.admin.product.interfaces.dto.SearchProductRequest
import com.fastcampus.commerce.admin.product.interfaces.dto.SearchProductResponse
import com.fastcampus.commerce.admin.product.interfaces.dto.UpdateProductRequest
import com.fastcampus.commerce.admin.product.interfaces.dto.UpdateProductResponse
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.response.EnumResponse
import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.product.interfaces.dto.ProductSellingStatusResponse
import org.springframework.data.domain.PageImpl
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

    @GetMapping
    fun searchProducts(
        @ModelAttribute request: SearchProductRequest,
        pageable: Pageable,
    ): PagedData<SearchProductResponse> {
        val data = SearchProductResponse(
            id = 1,
            name = "스타벅스 캡슐커피",
            price = 10000,
            quantity = 10,
            intensity = "1",
            cupSize = "25ml",
            sellingStatus = "판매중",
        )
        return PagedData.of(PageImpl(listOf(data), pageable, 1))
    }

    @GetMapping("/{productId}")
    fun getProduct(
        @PathVariable productId: Long,
    ): ProductResponse {
        if (productId != 1L) {
            throw CoreException(AdminProductErrorCode.PRODUCT_NOT_EXISTS)
        }
        return ProductResponse(
            id = productId,
            name = "스타벅스 캡슐커피",
            price = 10000,
            quantity = 10,
            thumbnail = "https://example.com/thumbnail.jpg",
            detailImage = "https://example.com/detail.jpg",
            intensityId = 1,
            cupSizeId = 10,
            sellingStatusCode = "ON_SALE",
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
        if (productId != 1L) {
            throw CoreException(AdminProductErrorCode.PRODUCT_NOT_EXISTS)
        }
        return UpdateProductResponse(productId)
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(
        @PathVariable productId: Long,
    ): DeleteProductResponse {
        if (productId != 1L) {
            throw CoreException(AdminProductErrorCode.PRODUCT_NOT_EXISTS)
        }
        return DeleteProductResponse()
    }
}
