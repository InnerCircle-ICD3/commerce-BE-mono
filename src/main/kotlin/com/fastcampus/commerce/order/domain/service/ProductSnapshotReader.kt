package com.fastcampus.commerce.order.domain.service

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.order.domain.entity.ProductSnapshot
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.order.infrastructure.ProductSnapshotRepository
import org.springframework.stereotype.Component

@Component
class ProductSnapshotReader(
    private val productSnapshotRepository: ProductSnapshotRepository,
) {
    fun getById(id: Long): ProductSnapshot {
        return productSnapshotRepository.findById(id)
            .orElseThrow { CoreException(OrderErrorCode.PRODUCT_SNAPSHOT_NOT_FOUND) }
    }

    fun findLatestByProductId(productId: Long): ProductSnapshot? {
        return productSnapshotRepository.findByProductId(productId).orElse(null)
    }
}
