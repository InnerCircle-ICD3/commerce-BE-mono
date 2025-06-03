package com.fastcampus.commerce.product.domain.repository

import com.fastcampus.commerce.product.domain.entity.Product
import java.util.Optional

interface ProductRepository {
    fun save(product: Product): Product

    fun findById(productId: Long): Optional<Product>
}
