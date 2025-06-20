package com.fastcampus.commerce.user.domain.repository

import com.fastcampus.commerce.user.domain.entity.UserOauth2Connection
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserOauth2ConnectionRepository : JpaRepository<UserOauth2Connection, Long> {
    fun findByProviderIdAndOauth2Id(providerId: Long, token: String): Optional<UserOauth2Connection>

    fun deleteByUserId(userId: Long)
}
