package com.fastcampus.commerce.review.domain.repository

import com.fastcampus.commerce.review.domain.model.ReviewInfoFlat
import com.fastcampus.commerce.review.domain.model.SearchUserReviewCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserReviewRepository {
    fun getReviewsBy(condition: SearchUserReviewCondition, pageable: Pageable): Page<ReviewInfoFlat>
}
