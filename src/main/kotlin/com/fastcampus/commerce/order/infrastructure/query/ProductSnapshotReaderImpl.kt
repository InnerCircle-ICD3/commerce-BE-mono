package com.fastcampus.commerce.order.infrastructure.query

import com.fastcampus.commerce.order.application.query.ProductSnapshotReader
import com.fastcampus.commerce.order.domain.entity.ProductSnapshot
import com.fastcampus.commerce.order.infrastructure.repository.ProductSnapshotRepository
import org.springframework.stereotype.Component

@Component
class ProductSnapshotReaderImpl(
    private val productSnapshotRepository: ProductSnapshotRepository,
) : ProductSnapshotReader {
    override fun getById(id: Long): ProductSnapshot {
        return productSnapshotRepository.findById(id)
            .orElseThrow { NoSuchElementException("ID '$id'에 해당하는 ProductSnapshot을 찾을 수 없습니다.") }
    }
}
