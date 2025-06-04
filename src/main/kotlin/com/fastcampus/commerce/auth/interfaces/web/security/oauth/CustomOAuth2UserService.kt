package com.fastcampus.commerce.auth.interfaces.web.security.oauth

import com.fastcampus.commerce.auth.infrastructure.security.oauth.config.NaverOAuth2Properties
import com.fastcampus.commerce.user.api.controller.UserController
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

private val log = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)

data class NaverUserResponse(
    val id: String,
    val email: String,
    val nickname: String?,
    val name: String?,
    val profileImage: String?,
)

@Service
class CustomOAuth2UserService(
    private val userController: UserController,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private val delegate = DefaultOAuth2UserService()

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = delegate.loadUser(userRequest)
        val responseMap = oAuth2User.attributes["response"] as? Map<*, *>
            ?: throw IllegalStateException("Missing Naver response")

        val naverUser = NaverUserResponse(
            id = responseMap["id"]?.toString() ?: "",
            name = responseMap["name"]?.toString(),
            email = responseMap["email"].toString(),
            nickname = responseMap["nickname"]?.toString(),
            profileImage = responseMap["profile_image"]?.toString(),
        )

        log.info("naverUser=$naverUser")

        // User 모듈에 API to API 호출
        val userDto = userController.saveOrUpdateUser(naverUser)

        // 이후 Security에 등록할 Principal 반환
        return CustomUserPrincipal.of(userDto)
    }
}
