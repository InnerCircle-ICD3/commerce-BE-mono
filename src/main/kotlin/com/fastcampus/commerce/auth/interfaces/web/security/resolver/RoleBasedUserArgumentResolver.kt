package com.fastcampus.commerce.auth.interfaces.web.security.resolver

import com.fastcampus.commerce.auth.interfaces.web.security.annotation.WithRoles
import com.fastcampus.commerce.common.error.AuthErrorCode
import com.fastcampus.commerce.common.error.CoreException
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
    private val userService: UserService
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        println("supportsParameter in")
        println("supportsParameter parameter: ${parameter.parameterName}")
        return parameter.hasParameterAnnotation(WithRoles::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        println("resolveArgument in")
        println("resolveArgument parameter: ${parameter.parameterName}")
        val userId = SecurityContextHolder.getContext().authentication?.name?.toLongOrNull()
            ?: throw CoreException(AuthErrorCode.UNAUTHENTICATED)
        println("resolveArgument userId: $userId")

        val withRole = parameter.getParameterAnnotation(WithRoles::class.java)!!
        println("resolveArgument withRole: ${withRole.value.joinToString()}")
        val requiredRoles = withRole.value // Array<UserRole>
        println("resolveArgument requiredRoles: ${requiredRoles.joinToString()}")

        val user = userService.findById(userId)
        println("resolveArgument user: $user")

        if (!userService.hasRole(userId, requiredRoles)) {
            throw AccessDeniedException("Required role: ${requiredRoles.joinToString()}")
        }

        return user
    }
}
