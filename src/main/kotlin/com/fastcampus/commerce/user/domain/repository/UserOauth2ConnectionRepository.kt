package com.fastcampus.commerce.user.domain.repository

import com.fastcampus.commerce.user.domain.entity.UserOauth2Connection
import org.springframework.data.jpa.repository.JpaRepository

interface UserOauth2ConnectionRepository : JpaRepository<UserOauth2Connection, Long>
