package com.fastcampus.commerce.review.infrastructure

import com.fastcampus.commerce.review.domain.entity.ReviewReply
import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewReplyJpaRepository : JpaRepository<ReviewReply, Long> {
    fun findByReviewId(replyId: Long): Optional<ReviewReply>
}
