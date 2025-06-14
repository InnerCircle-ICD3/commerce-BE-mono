package com.fastcampus.commerce.review.infrastructure

import com.fastcampus.commerce.common.error.CommonErrorCode
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.product.domain.entity.QProduct.product
import com.fastcampus.commerce.review.domain.entity.QReview.review
import com.fastcampus.commerce.review.domain.entity.QReviewReply.reviewReply
import com.fastcampus.commerce.review.domain.entity.Review
import com.fastcampus.commerce.review.domain.model.QReviewInfoFlat
import com.fastcampus.commerce.review.domain.model.ReviewInfoFlat
import com.fastcampus.commerce.review.domain.model.SearchUserReviewCondition
import com.fastcampus.commerce.review.domain.repository.UserReviewRepository
import com.fastcampus.commerce.user.domain.entity.QUser.user
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class UserReviewRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : UserReviewRepository, QuerydslRepositorySupport(Review::class.java) {
    override fun getReviewsBy(condition: SearchUserReviewCondition, pageable: Pageable): Page<ReviewInfoFlat> {
        val whereCondition = BooleanBuilder()
            .and(userIdEq(condition.userId))
            .and(createdAtBetween(condition.from, condition.to))

        val query = queryFactory
            .select(
                QReviewInfoFlat(
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

    private fun userIdEq(userId: Long) = user.id.eq(userId)

    private fun createdAtBetween(from: LocalDate?, to: LocalDate?) =
        if (from == null || to == null) {
            null
        } else {
            val fromDateTime = from.atStartOfDay()
            val toDateTime = to.plusDays(1).atStartOfDay()

            review.createdAt.goe(fromDateTime).and(review.createdAt.lt(toDateTime))
        }
}
