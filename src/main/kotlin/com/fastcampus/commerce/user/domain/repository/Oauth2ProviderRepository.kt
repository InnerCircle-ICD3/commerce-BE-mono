package com.fastcampus.commerce.user.domain.repository

import com.fastcampus.commerce.user.domain.entity.Oauth2Provider
import org.springframework.data.jpa.repository.JpaRepository

interface Oauth2ProviderRepository : JpaRepository<Oauth2Provider, Long> {
    fun findByName(name: String): Oauth2Provider?
}
