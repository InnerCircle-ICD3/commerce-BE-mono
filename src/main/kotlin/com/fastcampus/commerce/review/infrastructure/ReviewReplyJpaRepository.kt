package com.fastcampus.commerce.review.infrastructure

import com.fastcampus.commerce.review.domain.entity.ReviewReply
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ReviewReplyJpaRepository : JpaRepository<ReviewReply, Long> {
    fun findByReviewId(replyId: Long): Optional<ReviewReply>
}
