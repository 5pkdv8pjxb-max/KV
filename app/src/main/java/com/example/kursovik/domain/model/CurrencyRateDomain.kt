package com.example.kursovik.domain.model

data class CurrencyRateDomain(
    val charCode: String,
    val name: String,
    val value: Double,
    val nominal: Int,
    val valuePerUnit: Double,
    val date: String,
    val flagEmoji: String,
    val countryName: String,
    val updatedAt: Long = 0L
)
