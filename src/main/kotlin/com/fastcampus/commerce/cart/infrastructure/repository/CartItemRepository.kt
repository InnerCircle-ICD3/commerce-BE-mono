package com.fastcampus.commerce.cart.infrastructure.repository

import com.fastcampus.commerce.cart.domain.entity.CartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CartItemRepository : JpaRepository<CartItem, Long> {
    fun findByUserId(userId: Long): List<CartItem>

    fun findByUserIdAndProductId(userId: Long, productId: Long): CartItem?

    @Modifying
    @Query("UPDATE CartItem c SET c.deletedAt = :now WHERE c.id IN :cartItemIds")
    fun softDeleteByIds(cartItemIds: List<Long>, now: LocalDateTime = LocalDateTime.now())

    fun findAllByUserId(userId: Long): List<CartItem>?

    fun findByUserIdAndId(userId: Long, cartItemId: Long): CartItem?

    fun getAllByUserIdAndIdIn(userId: Long, cartItemIds: Set<Long>): List<CartItem>
}
