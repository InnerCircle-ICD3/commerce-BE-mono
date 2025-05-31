package com.fastcampus.commerce.product.interfaces.dto

import com.fastcampus.commerce.common.response.CodeResponse

data class ProductCategoryResponse(
    val intensities: List<CodeResponse>,
    val cupSizes: List<CodeResponse>,
)
