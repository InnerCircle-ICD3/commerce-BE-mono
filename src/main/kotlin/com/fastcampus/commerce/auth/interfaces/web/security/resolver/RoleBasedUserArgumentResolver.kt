package com.fastcampus.commerce.auth.interfaces.web.security.resolver

import com.fastcampus.commerce.auth.interfaces.web.security.model.LoginUser
import com.fastcampus.commerce.auth.interfaces.web.security.model.WithRoles
import com.fastcampus.commerce.user.api.service.UserService
import org.springframework.core.MethodParameter
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class RoleBasedUserArgumentResolver(
    private val userService: UserService,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(WithRoles::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): LoginUser {
        val userId = SecurityContextHolder.getContext().authentication?.name?.toLongOrNull()
            ?: 1L // throw CoreException(AuthErrorCode.UNAUTHENTICATED)

        val withRole = parameter.getParameterAnnotation(WithRoles::class.java)!!
        val requiredRoles = withRole.value

        val user = userService.getUser(userId)
        if (!userService.hasRole(userId, requiredRoles)) {
            throw AccessDeniedException("Required role: ${requiredRoles.joinToString()}")
        }
        return LoginUser(userId, user.externalId)
    }
}
