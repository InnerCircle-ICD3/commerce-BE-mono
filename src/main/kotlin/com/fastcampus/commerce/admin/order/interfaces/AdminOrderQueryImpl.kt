package com.fastcampus.commerce.admin.order.interfaces

import com.fastcampus.commerce.admin.order.infrastructure.request.AdminOrderSearchRequest
import com.fastcampus.commerce.admin.order.infrastructure.response.AdminOrderListResponse
import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.entity.QOrder
import com.fastcampus.commerce.order.domain.entity.QOrderItem
import com.fastcampus.commerce.order.domain.entity.QProductSnapshot
import com.fastcampus.commerce.user.domain.entity.QUser
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class AdminOrderQueryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : AdminOrderQuery {
    override fun searchOrders(
        search: AdminOrderSearchRequest,
        pageable: Pageable,
        sort: Sort
    ): Page<AdminOrderListResponse> {
        /*val qOrder = QOrder.order
        val qUser = QUser.user
        val qOrderItem = QOrderItem.orderItem
        val qProductSnapshot = QProductSnapshot.productSnapshot

        val query = jpaQueryFactory
            .select(
                Projections.constructor(
                    AdminOrderListResponse::class.java,
                    qOrder.id,
                    qOrder.orderNumber,
                    qOrderItem.name, // 요약 or 상품명/수량 등
                    qOrder.createdAt,
                    qUser.name,
                    qOrder.finalTotalPrice,
                    qOrder.paidAt,
                    qOrder.status.stringValue()
                )
            )
            .from(qOrder)
            .leftJoin(qUser).on(qOrder.userId.eq(qUser.id))
            .leftJoin(qOrderItem).on(qOrder.id.eq(qOrderItem.orderId))
        // 필요에 따라 ProductSnapshot 등 추가 조인

        // --- 동적 where ---
        val conditions = mutableListOf<BooleanExpression>()
        search.keyword?.let { keyword ->
            conditions.add(
                qOrder.orderNumber.containsIgnoreCase(keyword)
                    .or(qUser.name.containsIgnoreCase(keyword))
                    .or(qOrderItem.name.containsIgnoreCase(keyword))
            )
        }
        search.status?.let { status ->
            conditions.add(qOrder.status.eq(OrderStatus.valueOf(status)))
        }
        search.dateFrom?.let { from ->
            conditions.add(qOrder.createdAt.goe(from.atStartOfDay()))
        }
        search.dateTo?.let { to ->
            conditions.add(qOrder.createdAt.lt(to.plusDays(1).atStartOfDay()))
        }

        if (conditions.isNotEmpty()) {
            query.where(*conditions.toTypedArray())
        }

        // --- 동적 정렬 (컬럼별 order by) ---
        if (sort.isSorted) {
            sort.forEach {
                val path = when (it.property) {
                    "orderNumber" -> qOrder.orderNumber
                    "orderDate" -> qOrder.createdAt
                    "customerName" -> qUser.name
                    "totalAmount" -> qOrder.finalTotalPrice
                    "status" -> qOrder.status
                    else -> qOrder.id
                }
                query.orderBy(
                    if (it.isAscending) path.asc() else path.desc()
                )
            }
        } else {
            query.orderBy(qOrder.id.desc())
        }

        // --- 페이징 ---
        query.offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val results = query.fetch()
        val count = query.fetchCount()

        return PageImpl(results, pageable, count)*/
        return PageImpl(emptyList())
    }
}
