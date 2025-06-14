package com.fastcampus.commerce.admin.product.interfaces.response

import com.fastcampus.commerce.admin.product.application.response.SearchAdminProductResponse
import com.fastcampus.commerce.product.domain.entity.SellingStatus

data class SearchAdminProductApiResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val thumbnail: String,
    val status: SellingStatus,
    val stock: Int,
    val intensity: String,
    val cupSize: String,
) {
    companion object {
        fun from(response: SearchAdminProductResponse): SearchAdminProductApiResponse =
            SearchAdminProductApiResponse(
                id = response.id,
                name = response.name,
                price = response.price,
                thumbnail = response.thumbnail,
                status = response.status,
                stock = response.stock,
                intensity = response.intensity,
                cupSize = response.cupSize,
            )
    }
}
