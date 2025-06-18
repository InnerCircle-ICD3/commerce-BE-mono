package com.fastcampus.commerce.review.infrastructure

import com.fastcampus.commerce.common.error.CommonErrorCode
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.entity.QReview.review
import com.fastcampus.commerce.review.domain.entity.QReviewReply.reviewReply
import com.fastcampus.commerce.review.domain.entity.Review
import com.fastcampus.commerce.review.domain.model.ProductReviewFlat
import com.fastcampus.commerce.review.domain.model.ProductReviewRating
import com.fastcampus.commerce.review.domain.repository.ProductReviewRepository
import com.fastcampus.commerce.user.domain.entity.QUser.user
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.Expressions.nullExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class ProductReviewRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ProductReviewRepository, QuerydslRepositorySupport(Review::class.java) {
    override fun getProductReviews(productId: Long, pageable: Pageable): Page<ProductReviewFlat> {
        val query = queryFactory
            .select(
                Projections.constructor(
                    ProductReviewFlat::class.java,
                    review.id,
                    review.rating,
                    review.content,
                    review.createdAt,
                    reviewReply.content,
                    reviewReply.createdAt,
                    user.externalId,
                    user.nickname,
                ),
            )
            .from(review)
            .leftJoin(reviewReply).on(review.id.eq(reviewReply.reviewId))
            .join(user).on(review.userId.eq(user.id))
            .where(review.productId.eq(productId))

        val pagingQuery = querydsl?.applyPagination(pageable, query)
            ?: throw CoreException(CommonErrorCode.SERVER_ERROR)

        return PageableExecutionUtils.getPage(
            pagingQuery.fetch(),
            pageable
        ) {
            queryFactory
                .select(review.id.count())
                .from(review)
                .leftJoin(reviewReply).on(review.id.eq(reviewReply.reviewId))
                .join(user).on(review.userId.eq(user.id))
                .where(review.productId.eq(productId))
                .fetchOne() ?: 0L
        }
    }

    override fun getProductReviewRating(productId: Long): ProductReviewRating? {
        return queryFactory
            .select(
                Projections.constructor(
                    ProductReviewRating::class.java,
                    Expressions.template(Double::class.java, "round({0}, 2)", review.rating.avg().coalesce(0.0)),
                    review.count(),
                    review.rating.`when`(1).then(1).otherwise(nullExpression()).count(),
                    review.rating.`when`(2).then(1).otherwise(nullExpression()).count(),
                    review.rating.`when`(3).then(1).otherwise(nullExpression()).count(),
                    review.rating.`when`(4).then(1).otherwise(nullExpression()).count(),
                    review.rating.`when`(5).then(1).otherwise(nullExpression()).count(),
                ),
            )
            .from(review)
            .where(review.productId.eq(productId))
            .groupBy(review.productId)
            .fetchOne()
    }
}
