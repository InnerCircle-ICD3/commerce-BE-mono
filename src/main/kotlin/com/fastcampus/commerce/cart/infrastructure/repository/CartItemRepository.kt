package com.fastcampus.commerce.cart.infrastructure.repository

import com.fastcampus.commerce.cart.domain.entity.CartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepository : JpaRepository<CartItem, Long> {
    fun findByUserId(userId: Long): List<CartItem>

    fun findByUserIdAndProductId(userId: Long, productId: Long): CartItem?
}
