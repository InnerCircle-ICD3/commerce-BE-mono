package com.fastcampus.commerce.payment.domain.entity

enum class PaymentMethod(val label: String) {
    MOCK("테스트"),
    TOSS_PAY("토스페이");

    companion object {
        fun fromCode(code: String): PaymentMethod? =
            values().find { it.name == code }
    }
}
