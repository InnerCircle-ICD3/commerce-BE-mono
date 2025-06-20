package com.fastcampus.commerce.user.domain.enums

enum class UserRole(
    val label: String,
) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    SUPER_ADMIN("ROLE_SUPER_ADMIN"),

    ;

    companion object {
        fun from(value: String) = UserRole.entries.firstOrNull { it.label == value }
    }
}
