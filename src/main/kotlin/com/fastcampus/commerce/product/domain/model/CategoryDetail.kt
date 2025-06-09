package com.fastcampus.commerce.product.domain.model

import com.querydsl.core.annotations.QueryProjection

data class CategoryDetail
    @QueryProjection
    constructor(
        val groupId: Long,
        val groupTitle: String,
        val categoryId: Long,
        val categoryName: String,
        val sortOrder: Int,
    )
