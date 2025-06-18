package com.fastcampus.commerce.order.application.query

import com.fastcampus.commerce.order.domain.entity.ProductSnapshot

interface ProductSnapshotReader {
    fun getById(id: Long): ProductSnapshot
    fun findLatestByProductId(productId: Long):ProductSnapshot?
}
