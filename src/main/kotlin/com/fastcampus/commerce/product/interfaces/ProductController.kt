package com.fastcampus.commerce.product.interfaces

import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.product.application.MainProductService
import com.fastcampus.commerce.product.application.ProductQueryService
import com.fastcampus.commerce.product.interfaces.request.SearchProductApiRequest
import com.fastcampus.commerce.product.interfaces.response.CategoryApiResponse
import com.fastcampus.commerce.product.interfaces.response.MainProductApiResponse
import com.fastcampus.commerce.product.interfaces.response.ProductDetailApiResponse
import com.fastcampus.commerce.product.interfaces.response.SearchProductApiResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/products")
@RestController
class ProductController(
    private val productQueryService: ProductQueryService,
    private val mainProductService: MainProductService,
) {
    @GetMapping("/categories")
    fun getCategories(): CategoryApiResponse {
        val categoryResponses = productQueryService.getCategories()
        return CategoryApiResponse.from(categoryResponses)
    }

    @GetMapping
    fun searchProducts(
        @ModelAttribute request: SearchProductApiRequest,
        pageable: Pageable,
    ): PagedData<SearchProductApiResponse> {
        val products = productQueryService.getProducts(request.toServiceRequest(), pageable)
        return PagedData.of(products.map(SearchProductApiResponse::from))
    }

    @GetMapping("/{productId}")
    fun getProductDetail(
        @PathVariable productId: Long,
    ): ProductDetailApiResponse {
        return ProductDetailApiResponse.from(productQueryService.getProductDetail(productId))
    }

    @GetMapping("/main")
    fun getMainProducts(): MainProductApiResponse {
        val new = mainProductService.getNewProducts()
        val best = mainProductService.getBestProducts()
        return MainProductApiResponse(
            new = new.map(SearchProductApiResponse::from),
            best = best.map(SearchProductApiResponse::from),
        )
    }
}
