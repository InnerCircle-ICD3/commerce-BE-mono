package com.fastcampus.commerce.product.interfaces.request

import com.fastcampus.commerce.product.application.request.SearchProductRequest

data class SearchProductApiRequest(
    val name: String? = null,
    val intensityId: Long? = null,
    val cupSizeId: Long? = null,
) {
    fun toServiceRequest(): SearchProductRequest =
        SearchProductRequest(
            name = name,
            intensityId = intensityId,
            cupSizeId = cupSizeId,
        )
}
