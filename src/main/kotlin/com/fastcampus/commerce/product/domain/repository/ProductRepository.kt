package com.fastcampus.commerce.product.domain.repository

import com.fastcampus.commerce.product.domain.entity.Product

interface ProductRepository {
    fun save(product: Product): Product
}
