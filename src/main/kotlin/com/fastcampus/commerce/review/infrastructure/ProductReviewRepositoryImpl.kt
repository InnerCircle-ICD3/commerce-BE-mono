package com.fastcampus.commerce.review.infrastructure

import com.fastcampus.commerce.review.domain.entity.QReview.review
import com.fastcampus.commerce.review.domain.entity.QReviewReply.reviewReply
import com.fastcampus.commerce.review.domain.model.ProductReview
import com.fastcampus.commerce.review.domain.model.QAdminReply
import com.fastcampus.commerce.review.domain.model.QProductReview
import com.fastcampus.commerce.review.domain.repository.ProductReviewRepository
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class ProductReviewRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ProductReviewRepository {
    override fun getProductReviews(productId: Long, pageable: Pageable): Page<ProductReview> {
        val reviews = queryFactory
            .select(
                QProductReview(
                    review.id,
                    review.rating,
                    review.content,
                    review.createdAt,
                    QAdminReply(reviewReply.content, reviewReply.createdAt),
                ),
            )
            .from(review)
            .leftJoin(reviewReply).on(review.id.eq(reviewReply.reviewId))
            .where(review.productId.eq(productId))
            .orderBy(review.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageableExecutionUtils.getPage(reviews, pageable) {
            queryFactory
                .select(review.id.count())
                .from(review)
                .where(review.productId.eq(productId))
                .fetchOne() ?: 0L
        }
    }
}
