package com.example.kursovik.domain.model

data class ConversionResult(
    val fromCurrency: String,
    val toCurrency: String,
    val amount: Double,
    val result: Double,
    val rate: Double,
    val date: String,
    val updatedAt: Long = 0L
)
