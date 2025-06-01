package com.fastcampus.commerce.review.interfaces

import com.fastcampus.commerce.common.response.PagedData
import com.fastcampus.commerce.review.interfaces.dto.AdminReply
import com.fastcampus.commerce.review.interfaces.dto.MyReviewResponse
import com.fastcampus.commerce.review.interfaces.dto.SearchMyReviewRequest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class MyReviewController {
    @GetMapping("/users/me/reviews")
    fun getMyReview(
        @ModelAttribute request: SearchMyReviewRequest,
        pageable: Pageable,
    ): PagedData<MyReviewResponse> {
        val data = listOf(
            MyReviewResponse(
                reviewId = 2,
                rating = 3,
                content = "배송이 느렸어요 ㅠ",
                createdAt = LocalDateTime.now(),
                adminReply = AdminReply(
                    content = "죄송합니다.",
                    createdAt = LocalDateTime.now(),
                ),
                productId = 3,
                productName = "할리스 캡슐",
                productThumbnail = "https://example.com/thumbnail2.jpg",
            ),
        )
        return PagedData.of(PageImpl(data, pageable, 1))
    }
}
