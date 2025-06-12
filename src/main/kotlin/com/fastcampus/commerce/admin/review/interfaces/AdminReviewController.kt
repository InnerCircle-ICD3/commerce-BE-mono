package com.fastcampus.commerce.admin.review.interfaces

import com.fastcampus.commerce.admin.review.application.AdminReviewService
import com.fastcampus.commerce.admin.review.interfaces.request.RegisterReviewReplyApiRequest
import com.fastcampus.commerce.admin.review.interfaces.request.SearchReviewAdminApiRequest
import com.fastcampus.commerce.admin.review.interfaces.response.RegisterReviewReplyApiResponse
import com.fastcampus.commerce.admin.review.interfaces.response.SearchReviewAdminApiResponse
import com.fastcampus.commerce.common.response.PagedData
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/admin/reviews")
@RestController
class AdminReviewController(
    private val adminReviewService: AdminReviewService,
) {
    @GetMapping
    fun getReviews(
        @ModelAttribute request: SearchReviewAdminApiRequest,
        pageable: Pageable,
    ): PagedData<SearchReviewAdminApiResponse> {
        val responses = adminReviewService.search(request.toServiceRequest(), pageable)
        return PagedData.of(responses.map(SearchReviewAdminApiResponse.Companion::from))
    }

    @PostMapping("/{reviewId}/reply")
    fun registerReply(
        @PathVariable reviewId: Long,
        @RequestBody request: RegisterReviewReplyApiRequest,
    ): RegisterReviewReplyApiResponse {
        val adminId = 1L
        val replyId: Long = adminReviewService.registerReply(adminId, reviewId, request.content)
        return RegisterReviewReplyApiResponse(replyId)
    }
}
