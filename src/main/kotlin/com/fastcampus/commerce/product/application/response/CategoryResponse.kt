package com.fastcampus.commerce.product.application.response

import com.fastcampus.commerce.product.domain.model.CategoryDetail

data class CategoryResponse(
    val groupTitle: String,
    val id: Long,
    val name: String,
) {
    companion object {
        fun from(categoryDetail: CategoryDetail) =
            CategoryResponse(
                groupTitle = categoryDetail.groupTitle,
                id = categoryDetail.categoryId,
                name = categoryDetail.categoryName,
            )
    }
}
