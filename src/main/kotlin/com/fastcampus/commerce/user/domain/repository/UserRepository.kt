package com.fastcampus.commerce.user.domain.repository

import com.fastcampus.commerce.user.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
}
