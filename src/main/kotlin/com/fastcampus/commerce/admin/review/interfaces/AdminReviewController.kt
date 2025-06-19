package com.fastcampus.commerce.admin.review.interfaces

import com.fastcampus.commerce.admin.review.application.AdminReviewService
import com.fastcampus.commerce.admin.review.interfaces.request.RegisterReviewReplyApiRequest
import com.fastcampus.commerce.admin.review.interfaces.request.SearchReviewAdminApiRequest
import com.fastcampus.commerce.admin.review.interfaces.request.UpdateReviewReplyApiRequest
import com.fastcampus.commerce.admin.review.interfaces.response.RegisterReviewReplyApiResponse
import com.fastcampus.commerce.admin.review.interfaces.response.SearchReviewAdminApiResponse
import com.fastcampus.commerce.admin.review.interfaces.response.UpdateReviewReplyApiResponse
import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.review.interfaces.response.DeleteReviewReplyApiResponse
import com.fastcampus.commerce.user.domain.enums.UserRole
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

@RequestMapping("/admin/reviews")
@RestController
class AdminReviewController(
    private val adminReviewService: AdminReviewService,
) {
    @GetMapping
    fun getReviews(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @ModelAttribute request: SearchReviewAdminApiRequest,
        pageable: Pageable,
    ): PagedData<SearchReviewAdminApiResponse> {
        val responses = adminReviewService.search(request.toServiceRequest(), pageable)
        return PagedData.of(responses.map(SearchReviewAdminApiResponse.Companion::from))
    }

    @PostMapping("/{reviewId}/reply")
    fun registerReply(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable reviewId: Long,
        @RequestBody request: RegisterReviewReplyApiRequest,
    ): RegisterReviewReplyApiResponse {
        val replyId: Long = adminReviewService.registerReply(admin.id, reviewId, request.content)
        return RegisterReviewReplyApiResponse(replyId)
    }

    @PutMapping("/{reviewId}/reply")
    fun updateReply(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable reviewId: Long,
        @RequestBody request: UpdateReviewReplyApiRequest,
    ): UpdateReviewReplyApiResponse {
        adminReviewService.updateReply(admin.id, reviewId, request.content)
        return UpdateReviewReplyApiResponse(reviewId)
    }

    @DeleteMapping("/{reviewId}/reply")
    fun deleteReply(
        @WithRoles([UserRole.ADMIN]) admin: LoginUser,
        @PathVariable reviewId: Long,
    ): DeleteReviewReplyApiResponse {
        adminReviewService.deleteReply(admin.id, reviewId)
        return DeleteReviewReplyApiResponse()
    }
}
