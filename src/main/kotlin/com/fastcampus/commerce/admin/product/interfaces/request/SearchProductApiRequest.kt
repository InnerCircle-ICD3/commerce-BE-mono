package com.fastcampus.commerce.admin.product.interfaces.request

import com.fastcampus.commerce.product.domain.entity.SellingStatus

data class SearchProductApiRequest(
    val name: String? = null,
    val intensityId: Long? = null,
    val cupSizeId: Long? = null,
    val status: SellingStatus? = null,
)
