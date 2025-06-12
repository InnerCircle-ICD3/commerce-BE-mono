package com.fastcampus.commerce.review.infrastructure

import com.fastcampus.commerce.common.error.CommonErrorCode
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.QProduct.product
import com.fastcampus.commerce.review.domain.entity.QReview.review
import com.fastcampus.commerce.review.domain.entity.QReviewReply.reviewReply
import com.fastcampus.commerce.review.domain.entity.Review
import com.fastcampus.commerce.review.domain.entity.ReviewReply
import com.fastcampus.commerce.review.domain.model.QReviewAdminInfoFlat
import com.fastcampus.commerce.review.domain.model.ReviewAdminInfoFlat
import com.fastcampus.commerce.review.domain.model.SearchReviewAdminCondition
import com.fastcampus.commerce.review.domain.repository.ReviewAdminRepository
import com.fastcampus.commerce.user.domain.entity.QUser.user
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ReviewAdminRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val reviewReplyJpaRepository: ReviewReplyJpaRepository,
) : ReviewAdminRepository, QuerydslRepositorySupport(Review::class.java) {
    override fun searchReviews(condition: SearchReviewAdminCondition, pageable: Pageable): Page<ReviewAdminInfoFlat> {
        val whereCondition = BooleanBuilder()
            .and(productIdEq(condition.productId))
            .and(productNameContainsIgnore(condition.productName))
            .and(ratingEq(condition.rating))
            .and(contentContains(condition.content))
            .and(createdAtBetween(condition.from, condition.to))

        val query = queryFactory
            .select(
                QReviewAdminInfoFlat(
                    review.id,
                    review.rating,
                    review.content,
                    reviewReply.content,
                    reviewReply.createdAt,
                    user.id,
                    user.nickname,
                    product.id,
                    product.name,
                    review.createdAt,
                ),
            )
            .from(review)
            .join(user).on(review.userId.eq(user.id))
            .join(product).on(review.productId.eq(product.id))
            .leftJoin(reviewReply).on(review.id.eq(reviewReply.reviewId))
            .where(whereCondition)

        val pagingQuery = querydsl?.applyPagination(pageable, query)
            ?: throw CoreException(CommonErrorCode.SERVER_ERROR)

        return PageableExecutionUtils.getPage(
            pagingQuery.fetch(),
            pageable,
        ) {
            queryFactory
                .select(review.id.count())
                .from(review)
                .join(user).on(review.userId.eq(user.id))
                .join(product).on(review.productId.eq(product.id))
                .where(whereCondition)
                .fetchOne() ?: 0L
        }
    }

    override fun getReview(reviewId: Long): ReviewAdminInfoFlat? {
        return queryFactory
            .select(
                QReviewAdminInfoFlat(
                    review.id,
                    review.rating,
                    review.content,
                    reviewReply.content,
                    reviewReply.createdAt,
                    user.id,
                    user.nickname,
                    product.id,
                    product.name,
                    review.createdAt,
                ),
            )
            .from(review)
            .join(user).on(review.userId.eq(user.id))
            .join(product).on(review.productId.eq(product.id))
            .leftJoin(reviewReply).on(review.id.eq(reviewReply.reviewId))
            .where(review.id.eq(reviewId))
            .fetchOne()
    }

    override fun registerReply(reviewReply: ReviewReply): ReviewReply {
        return reviewReplyJpaRepository.save(reviewReply)
    }

    private fun productIdEq(productId: Long?) = if (productId == null) null else product.id.eq(productId)

    private fun productNameContainsIgnore(productName: String?): BooleanExpression? =
        if (productName.isNullOrEmpty()) {
            null
        } else {
            product.name.containsIgnoreCase(productName)
        }

    private fun ratingEq(rating: Int?) = if (rating == null) null else review.rating.eq(rating)

    private fun contentContains(content: String?) =
        if (content.isNullOrEmpty()) {
            null
        } else {
            review.content.contains(content)
        }

    private fun createdAtBetween(from: LocalDate?, to: LocalDate?) =
        if (from == null || to == null) {
            null
        } else {
            val fromDateTime = from.atStartOfDay()
            val toDateTime = to.plusDays(1).atStartOfDay()

            review.createdAt.goe(fromDateTime).and(review.createdAt.lt(toDateTime))
        }
}
