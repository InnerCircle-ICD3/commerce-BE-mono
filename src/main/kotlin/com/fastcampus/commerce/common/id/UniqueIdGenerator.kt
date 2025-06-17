package com.fastcampus.commerce.common.id

import io.viascom.nanoid.NanoId
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object UniqueIdGenerator {
    private const val PAYMENT_PREFIX = "PAY"
    private const val ORDER_PREFIX = "ORD"
    private const val USER_PREFIX = "USR"

    private fun generateUniqueId(prefix: String, date: LocalDate): String {
        val dateStr = date.format(DateTimeFormatter.BASIC_ISO_DATE)
        return "$prefix$dateStr${NanoId.generate(9, "0123456789")}"
    }

    fun generatePaymentNumber(date: LocalDate) = generateUniqueId(PAYMENT_PREFIX, date)

    fun generateOrderNumber(date: java.time.LocalDate)  = generateUniqueId(ORDER_PREFIX, date)
}
