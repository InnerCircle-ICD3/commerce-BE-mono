package com.fastcampus.commerce.admin.review.interfaces

import com.fastcampus.commerce.admin.review.error.AdminReviewErrorCode
import com.fastcampus.commerce.admin.review.interfaces.dto.AdminReply
import com.fastcampus.commerce.admin.review.interfaces.dto.AppendReviewReplyRequest
import com.fastcampus.commerce.admin.review.interfaces.dto.AppendReviewReplyResponse
import com.fastcampus.commerce.admin.review.interfaces.dto.DeleteReviewReplyResponse
import com.fastcampus.commerce.admin.review.interfaces.dto.SearchReviewRequest
import com.fastcampus.commerce.admin.review.interfaces.dto.SearchReviewResponse
import com.fastcampus.commerce.admin.review.interfaces.dto.UpdateReviewReplyRequest
import com.fastcampus.commerce.admin.review.interfaces.dto.UpdateReviewReplyResponse
import com.fastcampus.commerce.common.error.CoreException
import com.fastcampus.commerce.common.response.PagedData
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RequestMapping("/admin/reviews")
@RestController
class AdminReviewController {
    @GetMapping
    fun searchReview(
        @ModelAttribute request: SearchReviewRequest,
        pageable: Pageable,
    ): PagedData<SearchReviewResponse> {
        val data = SearchReviewResponse(
            reviewId = 1,
            rating = 5,
            content = "굿",
            createdAt = LocalDateTime.now(),
            adminReply = AdminReply("ㅎㅎ", LocalDateTime.now()),
            productId = 1,
            productName = "스타벅스 캡슐",
            productThumbnail = "https://example.com/thumbnail.jpg",
        )
        return PagedData.of(PageImpl(listOf(data), pageable, 1))
    }

    @PostMapping("/{reviewId}/reply")
    fun appendReply(
        @PathVariable reviewId: Long,
        @RequestBody request: AppendReviewReplyRequest,
    ): AppendReviewReplyResponse {
        if (request.content.isNullOrBlank()) {
            throw CoreException(AdminReviewErrorCode.REVIEW_NOT_EXISTS)
        }
        return AppendReviewReplyResponse(1)
    }

    @PutMapping("/{reviewId}/reply/{replyId}")
    fun updateReply(
        @PathVariable reviewId: Long,
        @PathVariable replyId: Long,
        @RequestBody request: UpdateReviewReplyRequest,
    ): UpdateReviewReplyResponse {
        if (replyId != 1L) {
            throw CoreException(AdminReviewErrorCode.REPLY_NOT_EXISTS)
        }
        return UpdateReviewReplyResponse(1)
    }

    @DeleteMapping("/{reviewId}/reply/{replyId}")
    fun appendReply(
        @PathVariable reviewId: Long,
        @PathVariable replyId: Long,
    ): DeleteReviewReplyResponse {
        if (replyId != 1L) {
            throw CoreException(AdminReviewErrorCode.REPLY_NOT_EXISTS)
        }
        return DeleteReviewReplyResponse()
    }
}
