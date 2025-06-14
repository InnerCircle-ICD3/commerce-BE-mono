package com.fastcampus.commerce.payment.infrastructure

import com.fastcampus.commerce.payment.domain.model.PgPaymentInfo
import com.fastcampus.commerce.payment.domain.service.PgClient
import org.springframework.stereotype.Component

@Component
class MockPgClient : PgClient {
    override fun getPaymentInfo(transactionId: String): PgPaymentInfo? {
        return PgPaymentInfo(10000, "OK")
    }
}
