package com.fastcampus.commerce.user.domain.enums

enum class UserRole(
    val label: String,
    val roleId: Long,
) {
    USER("ROLE_USER", 1L),
    ADMIN("ROLE_ADMIN", 2L),
    SUPER_ADMIN("ROLE_SUPER_ADMIN", 3L),

    ;

    companion object {
        fun from(value: String) = UserRole.entries.firstOrNull { it.label == value }
    }
}
