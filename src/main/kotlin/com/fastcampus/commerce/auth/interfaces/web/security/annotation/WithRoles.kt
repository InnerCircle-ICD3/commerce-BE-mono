package com.fastcampus.commerce.auth.interfaces.web.security.annotation

import com.fastcampus.commerce.user.domain.enums.UserRole

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class WithRoles(val value: Array<UserRole>)
