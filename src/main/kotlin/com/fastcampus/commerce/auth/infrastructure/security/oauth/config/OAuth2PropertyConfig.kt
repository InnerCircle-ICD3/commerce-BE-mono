package com.fastcampus.commerce.auth.infrastructure.security.oauth.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(NaverOAuth2Properties::class)
class OAuth2PropertyConfig
