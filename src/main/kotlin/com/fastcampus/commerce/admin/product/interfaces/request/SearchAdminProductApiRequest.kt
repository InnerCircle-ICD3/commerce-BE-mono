package com.fastcampus.commerce.admin.product.interfaces.request

import com.fastcampus.commerce.admin.product.application.request.SearchAdminProductRequest
import com.fastcampus.commerce.product.domain.entity.SellingStatus

data class SearchAdminProductApiRequest(
    val name: String? = null,
    val intensityId: Long? = null,
    val cupSizeId: Long? = null,
    val status: SellingStatus? = null,
) {
    fun toServiceRequest(): SearchAdminProductRequest =
        SearchAdminProductRequest(
            name = name,
            intensityId = intensityId,
            cupSizeId = cupSizeId,
            status = status,
        )
}
