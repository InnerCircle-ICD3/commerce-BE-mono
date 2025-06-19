package com.fastcampus.commerce.user.api.controller.request

import com.fastcampus.commerce.user.domain.entity.User

data class MyInfoResponse(
    val name: String,
    val email: String,
    val nickname: String,
) {
    companion object {
        fun of(user: User) =
            MyInfoResponse(
                name = user.name,
                email = user.email,
                nickname = user.nickname,
            )
    }
}
