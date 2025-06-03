package com.fastcampus.commerce.product.application

import com.fastcampus.commerce.product.domain.model.ProductRegister
import com.fastcampus.commerce.product.domain.model.ProductUpdater
import com.fastcampus.commerce.product.domain.service.CategoryStore
import com.fastcampus.commerce.product.domain.service.ProductStore
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

    @Transactional(readOnly = false)
    fun updateProduct(command: ProductUpdater) {
        productStore.updateProduct(command)
    }

    @Transactional(readOnly = false)
    fun updateInventory(command: ProductUpdater) {
        productStore.updateQuantityByProductId(command.id, command.quantity)
    }

    @Transactional(readOnly = false)
    fun deleteProduct(productId: Long) {
        productStore.deleteProductWithInventory(productId)
        categoryStore.removeProductCategories(productId)
    }
}
