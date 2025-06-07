package com.fastcampus.commerce.product.domain.model

data class SearchProductCondition(
    val name: String? = null,
    val categories: List<Long>? = null,
)
