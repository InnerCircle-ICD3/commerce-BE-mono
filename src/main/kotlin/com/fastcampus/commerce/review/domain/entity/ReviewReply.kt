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

@SQLDelete(sql = "update review_replies set deleted_at = now() where id = ?")
@SQLRestriction("deleted_at is null")
@Table(name = "review_replies")
@Entity
class ReviewReply(
    @Column(name = "review_id", nullable = false)
    val reviewId: Long,
    @Column(name = "replier_id", nullable = false)
    val replierId: Long,
    @Column(nullable = false, columnDefinition = "text")
    var content: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime

    @Column
    var deletedAt: LocalDateTime? = null

    init {
        validate()
    }

    private fun validate() {
        if (content.isBlank()) {
            throw CoreException(ReviewErrorCode.REPLY_CONTENT_EMPTY)
        }
    }
}
