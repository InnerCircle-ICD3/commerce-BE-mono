package com.fastcampus.commerce.product.domain.repository

import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.model.ProductInfo
import com.fastcampus.commerce.product.domain.model.SearchAdminProductCondition
import com.fastcampus.commerce.product.domain.model.SearchProductCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface ProductRepository {
    fun save(product: Product): Product

    fun findById(productId: Long): Optional<Product>

    fun delete(product: Product)

    fun searchProducts(condition: SearchProductCondition, pageable: Pageable): Page<ProductInfo>

    fun searchProductsForAdmin(condition: SearchAdminProductCondition, pageable: Pageable): Page<ProductInfo>
}
