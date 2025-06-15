package com.fastcampus.commerce.payment.domain.service

import com.fastcampus.commerce.payment.domain.model.PgPaymentInfo

interface PgClient {
    fun getPaymentInfo(transactionId: String): PgPaymentInfo?

    fun refund(transactionId: String, amount: Int)
}
