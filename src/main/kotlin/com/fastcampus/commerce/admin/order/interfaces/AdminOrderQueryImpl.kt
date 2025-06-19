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
import org.springframework.cglib.core.Local
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Repository
class AdminOrderQueryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : AdminOrderQuery {
    override fun searchOrders(search: AdminOrderSearchRequest, pageable: Pageable, sort: Sort): Page<AdminOrderListResponse> {
        val qOrder = QOrder.order
        val qUser = QUser.user
        val qOrderItem = QOrderItem.orderItem
        val qProductSnapshot = QProductSnapshot.productSnapshot

        val query = jpaQueryFactory
            .select(
                Projections.constructor(
                    AdminOrderListResponse::class.java,
                    qOrder.id,
                    qOrder.orderNumber,
                    qOrder.trackingNumber,
                    qProductSnapshot.name,
                    qOrderItem.quantity,
                    qOrderItem.unitPrice,
                    qOrder.createdAt,
                    qUser.name,
                    qOrder.totalAmount,
                    qOrder.paidAt,
                    qOrder.status.stringValue(),
                ),
            )
            .from(qOrder)
            .leftJoin(qUser).on(qOrder.userId.eq(qUser.id))
            .leftJoin(qOrderItem).on(qOrder.id.eq(qOrderItem.orderId))
            .leftJoin(qProductSnapshot).on(qOrderItem.productSnapshotId.eq(qProductSnapshot.id))

        // --- 동적 where ---
        val conditions = mutableListOf<BooleanExpression>()
        search.keyword?.let { keyword ->
            conditions.add(
                qOrder.orderNumber.containsIgnoreCase(keyword)
                    .or(qUser.name.containsIgnoreCase(keyword))
                    // .or(qOrderItem.name.containsIgnoreCase(keyword))
                    .or(qProductSnapshot.name.containsIgnoreCase(keyword)),
            )
        }
        search.status?.let { status ->
            conditions.add(qOrder.status.eq(OrderStatus.valueOf(status)))
        }
        search.dateFrom?.let { from ->
            val parse = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            conditions.add(qOrder.createdAt.goe(parse.atStartOfDay()))
        }
        search.dateTo?.let { to ->
            val parse = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            conditions.add(qOrder.createdAt.lt(parse.plusDays(1).atStartOfDay()))
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
                    "totalAmount" -> qOrder.totalAmount
                    "status" -> qOrder.status
                    else -> qOrder.createdAt
                }
                query.orderBy(
                    if (it.isAscending) path.asc() else path.desc(),
                )
            }
        } else {
            query.orderBy(qOrder.id.desc())
        }

        // --- 페이징 ---
        val results = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val count = jpaQueryFactory
            .select(qOrder.count())
            .from(qOrder)
            .leftJoin(qUser).on(qOrder.userId.eq(qUser.id))
            .leftJoin(qOrderItem).on(qOrder.id.eq(qOrderItem.orderId))
            .where(*conditions.toTypedArray())
            .fetchOne() ?: 0L

        return PageImpl(results, pageable, count)
    }
}
