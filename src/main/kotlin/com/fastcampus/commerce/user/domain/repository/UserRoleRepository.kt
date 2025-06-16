package com.fastcampus.commerce.user.domain.repository

import com.fastcampus.commerce.user.domain.entity.Role
import org.springframework.data.jpa.repository.JpaRepository

interface UserRoleRepository : JpaRepository<Role, Long> {
    fun findByCode(code: String): Role?
}
