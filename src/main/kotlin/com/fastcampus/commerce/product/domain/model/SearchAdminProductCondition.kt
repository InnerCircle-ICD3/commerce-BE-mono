package com.fastcampus.commerce.product.domain.model

import com.fastcampus.commerce.product.domain.entity.SellingStatus

data class SearchAdminProductCondition(
    val name: String? = null,
    val categories: List<Long>? = null,
    val status: SellingStatus? = null,
)
