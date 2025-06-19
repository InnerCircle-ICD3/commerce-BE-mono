package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.review.application.UserReviewService
import com.fastcampus.commerce.review.interfaces.request.UserReviewApiRequest
import com.fastcampus.commerce.review.interfaces.request.UserReviewApiResponse
import com.fastcampus.commerce.user.domain.enums.UserRole
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
class UserReviewController(
    private val userReviewService: UserReviewService,
) {
    @GetMapping("/reviews:byAuthor")
    fun getReviewsByAuthor(
        @WithRoles([UserRole.USER]) user: LoginUser,
        @ModelAttribute request: UserReviewApiRequest,
        pageable: Pageable,
    ): PagedData<UserReviewApiResponse> {
        return PagedData.of(
            userReviewService.getReviewsByAuthor(request.toServiceRequest(user.id), pageable)
                .map(UserReviewApiResponse::from),
        )
    }
}
