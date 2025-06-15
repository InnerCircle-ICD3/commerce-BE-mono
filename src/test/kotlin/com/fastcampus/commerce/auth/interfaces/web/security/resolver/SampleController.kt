package com.fastcampus.commerce.auth.interfaces.web.security.resolver

import com.fastcampus.commerce.auth.interfaces.web.security.annotation.WithRoles
import com.fastcampus.commerce.user.domain.entity.User
import com.fastcampus.commerce.user.domain.enums.UserRole
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController {
    @GetMapping("/api/test/with-roles")
    fun getWithRole(@WithRoles(arrayOf(UserRole.ADMIN)) user: User): ResponseEntity<String> {
        return ResponseEntity.ok("ok:${user.name}")
    }
}
