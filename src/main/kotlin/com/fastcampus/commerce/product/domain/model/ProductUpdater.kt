package com.fastcampus.commerce.product.domain.model

import com.fastcampus.commerce.product.domain.entity.SellingStatus

data class ProductUpdater(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val detailImage: String,
    val thumbnail: String,
    val categoryIds: List<Long>,
    val status: SellingStatus,
    val updaterId: Long,
)
