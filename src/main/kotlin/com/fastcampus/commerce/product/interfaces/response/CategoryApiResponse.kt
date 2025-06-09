package com.fastcampus.commerce.product.interfaces.response

import com.fastcampus.commerce.common.response.CodeResponse
import com.fastcampus.commerce.product.application.response.CategoryResponse
import com.fastcampus.commerce.product.domain.model.CategoryType

data class CategoryApiResponse(
    val cupSizes: List<CodeResponse>,
    val intensities: List<CodeResponse>,
) {
    companion object {
        fun from(categoryResponses: List<CategoryResponse>): CategoryApiResponse {
            return categoryResponses.toCategoryApiResponse()
        }

        private fun List<CategoryResponse>.toCategoryApiResponse(): CategoryApiResponse {
            val grouped = groupBy { it.groupTitle }
            return CategoryApiResponse(
                cupSizes = grouped.getCodeResponses(CategoryType.CUP_SIZE),
                intensities = grouped.getCodeResponses(CategoryType.INTENSITY),
            )
        }

        private fun Map<String, List<CategoryResponse>>.getCodeResponses(categoryType: CategoryType): List<CodeResponse> {
            return this[categoryType.groupTitle]
                ?.map { CodeResponse(id = it.id.toString(), label = it.name) }
                ?: emptyList()
        }
    }
}
