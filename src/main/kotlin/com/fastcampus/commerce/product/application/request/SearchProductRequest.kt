package com.fastcampus.commerce.product.application.request

import com.fastcampus.commerce.product.domain.model.SearchProductCondition

data class SearchProductRequest(
    val name: String? = null,
    val intensityId: Long? = null,
    val cupSizeId: Long? = null,
) {
    fun toCondition(): SearchProductCondition {
        return SearchProductCondition(
            name = name,
            categories = listOfNotNull(intensityId, cupSizeId),
        )
    }
}
