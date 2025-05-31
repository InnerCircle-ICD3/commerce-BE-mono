package com.fastcampus.commerce.product.interfaces

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.response.CodeResponse
import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.product.domain.error.ProductErrorCode
import com.fastcampus.commerce.product.interfaces.dto.ProductCategoryResponse
import com.fastcampus.commerce.product.interfaces.dto.ProductResponse
import com.fastcampus.commerce.product.interfaces.dto.SearchProductRequest
import com.fastcampus.commerce.product.interfaces.dto.SearchProductResponse
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/products")
@RestController
class ProductController {
    @GetMapping("/categories")
    fun getProductCategories(): ProductCategoryResponse {
        return ProductCategoryResponse(
            intensities = listOf(
                CodeResponse(1, "1"),
                CodeResponse(2, "2"),
                CodeResponse(3, "3"),
                CodeResponse(4, "4"),
                CodeResponse(5, "5"),
                CodeResponse(6, "6"),
                CodeResponse(7, "7"),
                CodeResponse(8, "8"),
                CodeResponse(9, "9"),
            ),
            cupSizes = listOf(
                CodeResponse(10, "25ml"),
                CodeResponse(11, "40ml"),
                CodeResponse(12, "80ml"),
                CodeResponse(13, "150ml"),
                CodeResponse(14, "230ml"),
                CodeResponse(15, "355ml"),
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
            thumbnail = "https://example.com/thumbnail.jpg",
            intensity = "1",
            cupSize = "25ml",
            isSoldOut = false,
        )
        return PagedData.of(PageImpl(listOf(data), pageable, 1))
    }

    @GetMapping("/{productId}")
    fun getProduct(
        @PathVariable productId: Long,
    ): ProductResponse {
        if (productId != 1L) {
            throw CoreException(ProductErrorCode.PRODUCT_NOT_FOUND)
        }
        return ProductResponse(
            id = productId,
            name = "스타벅스 캡슐커피",
            price = 10000,
            quantity = 10,
            thumbnail = "https://example.com/thumbnail.jpg",
            detailImage = "https://example.com/detail.jpg",
            intensity = "1",
            cupSize = "25ml",
            isSoldOut = false,
        )
    }
}
