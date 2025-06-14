package com.fastcampus.commerce.auth.filter

import com.fastcampus.commerce.auth.TokenProvider
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val accessToken = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return filterChain.doFilter(request, response)

        if (!tokenProvider.validateToken(accessToken)) {
            return filterChain.doFilter(request, response)
        }

        val authentication = tokenProvider.getAuthentication(accessToken)
        org.springframework.security.core.context.SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }
}
