package com.fastcampus.commerce.product.application

import com.fastcampus.commerce.admin.product.application.request.SearchAdminProductRequest
import com.fastcampus.commerce.admin.product.application.response.AdminProductDetailResponse
import com.fastcampus.commerce.admin.product.application.response.SearchAdminProductResponse
import com.fastcampus.commerce.product.application.request.SearchProductRequest
import com.fastcampus.commerce.product.application.response.CategoryResponse
import com.fastcampus.commerce.product.application.response.ProductDetailResponse
import com.fastcampus.commerce.product.application.response.SearchProductResponse
import com.fastcampus.commerce.product.domain.model.CategoryDetail
import com.fastcampus.commerce.product.domain.model.ProductCategoryInfo
import com.fastcampus.commerce.product.domain.service.CategoryReader
import com.fastcampus.commerce.product.domain.service.ProductReader
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProductQueryService(
    private val productReader: ProductReader,
    private val categoryReader: CategoryReader,
) {
    fun getCategories(): List<CategoryResponse> {
        val categories: List<CategoryDetail> = categoryReader.getCategoryDetail()
        return categories.map(CategoryResponse::from)
    }

    fun getProducts(request: SearchProductRequest, pageable: Pageable): Page<SearchProductResponse> {
        val productInfos = productReader.searchProducts(request.toCondition(), pageable)
        val productIds = productInfos.content.map { it.id }
        val categoryMap = categoryReader.getProductCategoryMap(productIds)
        return productInfos.map { productInfo ->
            val productCategoryInfo = categoryMap[productInfo.id] ?: ProductCategoryInfo.empty()
            SearchProductResponse.of(productInfo, productCategoryInfo)
        }
    }

    fun getProductDetail(productId: Long): ProductDetailResponse {
        val productInfo = productReader.getProductInfo(productId)
        val productCategoryInfo = categoryReader.getProductCategory(productId)
        return ProductDetailResponse.of(productInfo, productCategoryInfo)
    }

    fun searchProductsForAdmin(request: SearchAdminProductRequest, pageable: Pageable): Page<SearchAdminProductResponse> {
        val productInfos = productReader.searchProductsForAdmin(request.toCondition(), pageable)
        val productIds = productInfos.content.map { it.id }
        val categoryMap = categoryReader.getProductCategoryMap(productIds)
        return productInfos.map { productInfo ->
            val productCategoryInfo = categoryMap[productInfo.id] ?: ProductCategoryInfo.empty()
            SearchAdminProductResponse.of(productInfo, productCategoryInfo)
        }
    }

    fun getProductDetailForAdmin(productId: Long): AdminProductDetailResponse {
        val productInfo = productReader.getProductInfo(productId)
        val productCategoryInfo = categoryReader.getProductCategory(productId)
        return AdminProductDetailResponse.of(productInfo, productCategoryInfo)
    }

    fun getNewProducts(): List<SearchProductResponse> {
        TODO("Not yet implemented")
    }

    fun getBestProducts(): List<SearchProductResponse> {
        TODO("Not yet implemented")
    }
}
