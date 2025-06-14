package com.fastcampus.commerce.payment.application.request

data class PaymentProcessRequest(
    val orderNumber: String,
    val transactionId: String,
)
