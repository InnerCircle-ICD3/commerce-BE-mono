package com.fastcampus.commerce.order.application.review

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.order.domain.repository.OrderReviewRepository
import org.springframework.stereotype.Service

@Service
class OrderReviewService(
    private val orderReviewRepository: OrderReviewRepository,
) {
    fun getReviewInfo(orderNumber: String, orderItemId: Long): OrderReview {
        return orderReviewRepository.findOrderReview(orderNumber, orderItemId)
            ?: throw CoreException(OrderErrorCode.ORDER_DATA_FOR_REVIEW_NOT_FOUND)
    }
}
