package com.fastcampus.commerce.admin.review.interfaces

import com.fastcampus.commerce.admin.review.application.AdminReviewService
import com.fastcampus.commerce.admin.review.interfaces.request.SearchReviewAdminApiRequest
import com.fastcampus.commerce.admin.review.interfaces.response.SearchReviewAdminApiResponse
import com.fastcampus.commerce.common.response.PagedData
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
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
}
