package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.repository.ProductRepository
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val productJpaRepository: ProductJpaRepository,
) : ProductRepository {
    override fun save(product: Product): Product {
        return productJpaRepository.save(product)
    }
}
