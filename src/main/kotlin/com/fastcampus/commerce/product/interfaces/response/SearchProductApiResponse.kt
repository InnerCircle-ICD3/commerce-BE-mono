package com.fastcampus.commerce.product.interfaces.response

import com.fastcampus.commerce.product.application.response.SearchProductResponse

data class SearchProductApiResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val thumbnail: String,
    val detailImage: String,
    val intensity: String,
    val cupSize: String,
    val isSoldOut: Boolean,
) {
    companion object {
        fun from(response: SearchProductResponse): SearchProductApiResponse {
            return SearchProductApiResponse(
                id = response.id,
                name = response.name,
                price = response.price,
                quantity = response.quantity,
                thumbnail = response.thumbnail,
                detailImage = response.detailImage,
                intensity = response.intensity ?: "",
                cupSize = response.cupSize ?: "",
                isSoldOut = response.quantity <= 0,
            )
        }
    }
}
