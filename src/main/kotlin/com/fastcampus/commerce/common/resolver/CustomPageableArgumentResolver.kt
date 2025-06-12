package com.fastcampus.commerce.common.resolver

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.MethodParameter
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@ConfigurationProperties(prefix = "spring.data.web.pageable")
data class PageableProperties(
    val oneIndexedParameters: Boolean = false,
    val defaultPageSize: Int = 10,
    val maxPageSize: Int = 50,
)

@Component
class CustomPageableArgumentResolver(
    private val pageableProperties: PageableProperties,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter) = parameter.parameterType == Pageable::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Pageable {
        val page = extractPageNumber(webRequest.getParameter("page"))
        val size = extractPageSize(webRequest.getParameter("size"))
        val sort = extractSort(webRequest.getParameter("sort"))
        return PageRequest.of(page, size, sort)
    }

    private fun extractPageNumber(pageParam: String?): Int {
        val rawPage = pageParam?.toIntOrNull()
        val defaultPage = if (pageableProperties.oneIndexedParameters) 1 else 0
        val offset = if (pageableProperties.oneIndexedParameters) 1 else 0
        return maxOf(0, (rawPage ?: defaultPage) - offset)
    }

    private fun extractPageSize(sizeParam: String?): Int {
        return sizeParam?.toIntOrNull()
            ?.let { maxOf(1, minOf(pageableProperties.maxPageSize, it)) }
            ?: pageableProperties.defaultPageSize
    }

    private fun extractSort(sortParam: String?): Sort {
        val sortValue = sortParam ?: "-createdAt"
        return parseSort(sortValue)
    }

    private fun parseSort(sort: String): Sort {
        val orders = sort.split(",").map { field ->
            val trimmedField = field.trim()
            when {
                trimmedField.startsWith("-") -> Sort.Order.desc(trimmedField.substring(1))
                else -> Sort.Order.asc(trimmedField)
            }
        }
        return Sort.by(orders)
    }
}
