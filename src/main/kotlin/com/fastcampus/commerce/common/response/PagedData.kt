package com.fastcampus.commerce.common.response

import org.springframework.data.domain.Page

data class PagedData<T> private constructor(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long,
) {
    companion object {
        fun <S> of(page: Page<S>): PagedData<S> {
            return PagedData(
                content = page.content,
                page = page.number,
                size = page.size,
                totalPages = page.totalPages,
                totalElements = page.totalElements,
            )
        }
    }
}
