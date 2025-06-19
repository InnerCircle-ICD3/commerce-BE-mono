package com.fastcampus.commerce.order.interfaces.request

import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.model.MyOrderCondition
import java.time.LocalDateTime

data class SearchOrderApiRequest(
    val monthRange: Int? = null,
    val status: OrderStatus? = null,
) {
    fun toCondition(userId: Long, now: LocalDateTime): MyOrderCondition {
        val to = if (monthRange == null) null else now.toLocalDate()
        val from = if (monthRange == null) null else to?.minusMonths(monthRange.toLong())
        return MyOrderCondition(
            userId = userId,
            from = from,
            to = to,
            status = status,
        )
    }
}
