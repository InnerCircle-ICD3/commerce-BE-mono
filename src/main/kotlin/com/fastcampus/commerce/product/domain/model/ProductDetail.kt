package com.fastcampus.commerce.product.domain.model

import com.querydsl.core.annotations.QueryProjection

data class ProductInfo
    @QueryProjection
    constructor(
        val id: Long,
        val name: String,
        val price: Int,
        val quantity: Int,
        val thumbnail: String,
        val detailImage: String,
    )
