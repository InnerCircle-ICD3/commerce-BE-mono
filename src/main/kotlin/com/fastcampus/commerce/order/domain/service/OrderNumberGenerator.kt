package com.fastcampus.commerce.order.domain.service

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
object OrderNumberGenerator {
    fun generate(orderId: Long): String {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val encoded = base62Encode(orderId)
        return "ORD$date$encoded"
    }

    private val base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray()

    private fun base62Encode(number: Long): String {
        var n = number
        val sb = StringBuilder()
        while (n > 0) {
            sb.append(base62Chars[(n % 62).toInt()])
            n /= 62
        }
        return sb.reverse().toString()
    }
}
