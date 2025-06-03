package com.fastcampus.commerce.product.application

import com.fastcampus.commerce.product.domain.service.CategoryStore
import com.fastcampus.commerce.product.domain.service.ProductStore
import com.fastcampus.commerce.product.domain.service.dto.ProductRegister
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductCommandService(
    private val productStore: ProductStore,
    private val categoryStore: CategoryStore,
) {
    @Transactional(readOnly = false)
    fun register(command: ProductRegister): Long {
        val product = productStore.saveProductWithInventory(command)
        val productId = product.id!!
        categoryStore.mappingProductCategories(productId, command.categoryIds)
        return productId
    }
}
