package com.fastcampus.commerce.admin.product.application.response

import com.fastcampus.commerce.product.domain.entity.SellingStatus

data class SellingStatusResponse(
    val code: String,
    val label: String,
) {
    companion object {
        fun from(sellingStatus: SellingStatus) =
            SellingStatusResponse(
                code = sellingStatus.name,
                label = sellingStatus.label,
            )
    }
}
