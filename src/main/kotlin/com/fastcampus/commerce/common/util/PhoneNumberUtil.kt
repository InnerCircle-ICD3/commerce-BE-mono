package com.fastcampus.commerce.common.util

object PhoneNumberUtil {
    fun removeHyphens(phoneNumber: String): String = phoneNumber.replace("-", "")

    fun addHyphens(phoneNumber: String): String =
        when (phoneNumber.length) {
            10 -> "${phoneNumber.substring(0, 3)}-${phoneNumber.substring(3, 6)}-${phoneNumber.substring(6)}"
            11 -> "${phoneNumber.substring(0, 3)}-${phoneNumber.substring(3, 7)}-${phoneNumber.substring(7)}"
            else -> phoneNumber
        }
}
