package com.fastcampus.commerce.product.infrastructure

import com.fastcampus.commerce.order.domain.entity.OrderStatus
import com.fastcampus.commerce.order.domain.entity.QOrder.*
import com.fastcampus.commerce.order.domain.entity.QOrderItem.orderItem
import com.fastcampus.commerce.order.domain.entity.QProductSnapshot.productSnapshot
import com.fastcampus.commerce.product.domain.entity.Product
import com.fastcampus.commerce.product.domain.entity.QInventory.inventory
import com.fastcampus.commerce.product.domain.entity.QProduct.product
import com.fastcampus.commerce.product.domain.entity.QProductCategory.productCategory
import com.fastcampus.commerce.product.domain.entity.SellingStatus
import com.fastcampus.commerce.product.domain.model.ProductInfo
import com.fastcampus.commerce.product.domain.model.QProductInfo
import com.fastcampus.commerce.product.domain.model.SearchAdminProductCondition
import com.fastcampus.commerce.product.domain.model.SearchProductCondition
import com.fastcampus.commerce.product.domain.repository.ProductRepository
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

@Repository
class ProductRepositoryImpl(
    private val productJpaRepository: ProductJpaRepository,
    private val queryFactory: JPAQueryFactory,
) : ProductRepository {
    override fun save(product: Product): Product {
        return productJpaRepository.save(product)
    }

    override fun findById(productId: Long): Optional<Product> {
        return productJpaRepository.findById(productId)
    }

    override fun delete(product: Product) {
        productJpaRepository.delete(product)
    }

    override fun searchProducts(condition: SearchProductCondition, pageable: Pageable): Page<ProductInfo> {
        val whereCondition = BooleanBuilder()
            .and(productNameContainsIgnore(condition.name))
            .and(productCategoryIdsIn(condition.categories))

        val content = fetchProductDetails(whereCondition, pageable)

        return PageableExecutionUtils.getPage(content, pageable) {
            queryFactory
                .select(product.id.count())
                .from(product)
                .where(whereCondition)
                .fetchOne() ?: 0L
        }
    }

    override fun searchProductsForAdmin(condition: SearchAdminProductCondition, pageable: Pageable): Page<ProductInfo> {
        val whereCondition = BooleanBuilder()
            .and(productNameContainsIgnore(condition.name))
            .and(productStatusEq(condition.status))
            .and(productCategoryIdsIn(condition.categories))

        val content = fetchProductDetails(whereCondition, pageable)
        return PageableExecutionUtils.getPage(content, pageable) {
            queryFactory
                .select(product.id.count())
                .from(product)
                .where(whereCondition)
                .fetchOne() ?: 0L
        }
    }

    override fun findLatestProducts(limit: Int): List<ProductInfo> {
        val whereCondition = BooleanBuilder()
            .and(productStatusEq(SellingStatus.ON_SALE))

        return fetchProductDetails(whereCondition, PageRequest.of(0, limit))
    }

    override fun findBestProducts(baseDate: LocalDateTime, limit: Int): List<ProductInfo> {
        return queryFactory
            .select(
                QProductInfo(
                    product.id,
                    product.name,
                    product.price,
                    inventory.quantity,
                    product.thumbnail,
                    product.detailImage,
                    product.status,
                ),
            )
            .from(orderItem)
            .join(order).on(orderItem.orderId.eq(order.id))
            .join(productSnapshot).on(orderItem.productSnapshotId.eq(productSnapshot.id))
            .join(product).on(productSnapshot.productId.eq(product.id))
            .join(inventory).on(product.id.eq(inventory.productId))
            .where(
                order.status.`in`(
                    OrderStatus.PAID,
                    OrderStatus.SHIPPED,
                    OrderStatus.DELIVERED,
                ),
                product.status.eq(SellingStatus.ON_SALE),
                order.isDeleted.eq(false),
                product.isDeleted.eq(false),
                order.createdAt.goe(baseDate)
            )
            .groupBy(
                product.id, product.name, product.price,
                product.thumbnail, product.detailImage,
                product.status, inventory.quantity
            )
            .orderBy(orderItem.quantity.sumLong().desc())
            .limit(limit.toLong())
            .fetch()
    }

    private fun productNameContainsIgnore(productName: String?) =
        if (productName == null) {
            null
        } else {
            product.name.containsIgnoreCase(
                productName,
            )
        }

    private fun productStatusEq(status: SellingStatus?) = if (status == null) null else product.status.eq(status)

    private fun productCategoryIdsIn(categoryIds: List<Long>?) =
        if (categoryIds.isNullOrEmpty()) {
            null
        } else {
            val subQuery = JPAExpressions
                .select(productCategory.productId)
                .from(productCategory)
                .where(
                    productCategory.categoryId.`in`(categoryIds),
                    productCategory.isDeleted.isFalse,
                )
                .groupBy(productCategory.productId)
                .having(productCategory.categoryId.countDistinct().eq(categoryIds.size.toLong()))

            product.id.`in`(subQuery)
        }

    private fun fetchProductDetails(whereCondition: BooleanBuilder, pageable: Pageable): List<ProductInfo> {
        return queryFactory
            .select(
                QProductInfo(
                    product.id,
                    product.name,
                    product.price,
                    inventory.quantity,
                    product.thumbnail,
                    product.detailImage,
                    product.status,
                ),
            )
            .from(product)
            .join(inventory).on(product.id.eq(inventory.productId))
            .where(whereCondition)
            .orderBy(product.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }
}
