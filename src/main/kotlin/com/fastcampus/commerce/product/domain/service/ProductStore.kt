package com.fastcampus.commerce.product.domain.service

import com.fastcampus.commerce.product.domain.entity.Inventory
import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.repository.InventoryRepository
import com.fastcampus.commerce.product.domain.repository.ProductRepository
import com.fastcampus.commerce.product.domain.service.dto.ProductRegister
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductStore(
    private val productRepository: ProductRepository,
    private val inventoryRepository: InventoryRepository,
) {
    @Transactional(readOnly = false)
    fun saveProductWithInventory(command: ProductRegister): Product {
        val product = productRepository.save(command.toProduct())
        val productId = product.id!!
        inventoryRepository.save(Inventory(productId = productId, quantity = command.quantity))
        return product
    }
}
