package com.fastcampus.commerce.admin.product.interfaces.response

import com.fastcampus.commerce.admin.product.application.response.SearchAdminProductResponse
import com.fastcampus.commerce.product.domain.entity.SellingStatus

data class SearchAdminProductApiResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val thumbnail: String,
    val intensity: String,
    val cupSize: String,
    val status: SellingStatus,
) {
    companion object {
        fun from(response: SearchAdminProductResponse): SearchAdminProductApiResponse =
            SearchAdminProductApiResponse(
                id = response.id,
                name = response.name,
                price = response.price,
                quantity = response.quantity,
                thumbnail = response.thumbnail,
                intensity = response.intensity,
                cupSize = response.cupSize,
                status = response.status,
            )
    }
}
