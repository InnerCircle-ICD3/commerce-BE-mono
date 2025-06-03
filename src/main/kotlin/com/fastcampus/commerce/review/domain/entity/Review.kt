package com.fastcampus.commerce.review.domain.entity

import com.fastcampus.commerce.common.entity.BaseEntity
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.review.domain.error.ReviewErrorCode
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@SQLDelete(sql = "update reviews set is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted = false")
@Table(name = "reviews")
@Entity
class Review(
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(name = "order_item_id", nullable = false)
    val orderItemId: Long,
    @Column(name = "product_id", nullable = false)
    val productId: Long,
    @Column(nullable = false)
    var rating: Int,
    @Column(nullable = false, length = 1000)
    var content: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var isDeleted: Boolean = false

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime

    @Column
    var deletedAt: LocalDateTime? = null

    init {
        validate()
    }

    private fun validate() {
        validateContent()
        validateRating()
    }

    private fun validateContent() {
        if (this.content.isBlank()) {
            throw CoreException(ReviewErrorCode.CONTENT_EMPTY)
        }
    }

    private fun validateRating() {
        if (rating < 1 || rating > 5) {
            throw CoreException(ReviewErrorCode.INVALID_RATING)
        }
    }

    companion object {
        const val MAX_WRITTEN_DATE = 30L

        fun validateReviewWrittenDate(now: LocalDateTime, deliveredAt: LocalDateTime?) {
            if (deliveredAt == null) {
                throw CoreException(ReviewErrorCode.ORDER_NOT_DELIVERED)
            }
            if (deliveredAt.isBefore(now.minusDays(MAX_WRITTEN_DATE))) {
                throw CoreException(ReviewErrorCode.TOO_LATE)
            }
        }
    }
}
