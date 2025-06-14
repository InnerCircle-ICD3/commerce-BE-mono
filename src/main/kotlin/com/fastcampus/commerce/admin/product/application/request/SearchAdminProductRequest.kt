package com.fastcampus.commerce.admin.product.application.request

import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.product.domain.model.SearchAdminProductCondition

data class SearchAdminProductRequest(
    val name: String? = null,
    val intensityId: Long? = null,
    val cupSizeId: Long? = null,
    val status: SellingStatus? = null,
) {
    fun toCondition() =
        SearchAdminProductCondition(
            name = name,
            categories = listOfNotNull(intensityId, cupSizeId),
            status = status,
        )
}
