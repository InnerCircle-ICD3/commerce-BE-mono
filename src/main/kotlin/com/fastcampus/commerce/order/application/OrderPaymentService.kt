package com.fastcampus.commerce.order.application

import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.order.domain.entity.Order
import com.fastcampus.commerce.order.domain.error.OrderErrorCode
import com.fastcampus.commerce.order.domain.repository.OrderPaymentRepository
import org.springframework.stereotype.Service

@Service
class OrderPaymentService(
    private val orderPaymentRepository: OrderPaymentRepository,
) {
    fun getOrder(orderNumber: String): Order {
        return orderPaymentRepository.findOrderByOrderNumber(orderNumber)
            .orElseThrow { CoreException(OrderErrorCode.ORDER_NOT_FOUND) }
    }
}
