package com.fastcampus.commerce.user.domain.repository

import com.fastcampus.commerce.user.domain.entity.UserRoleConnection
import org.springframework.data.jpa.repository.JpaRepository

interface UserRoleConnectionRepository : JpaRepository<UserRoleConnection, Long> {
    fun findAllByUserId(userId: Long): List<UserRoleConnection>

    fun deleteByUserId(userId: Long)
}
