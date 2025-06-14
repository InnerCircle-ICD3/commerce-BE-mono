package com.fastcampus.commerce.product.interfaces.response

data class MainProductApiResponse(
    val new: List<SearchProductApiResponse>,
    val best: List<SearchProductApiResponse>,
)
