package com.example.kursovik.domain.model

data class CurrencyDomain(
    val charCode: String,
    val numCode: String,
    val nominal: Int,
    val name: String,
    val value: Double,
    val valuePerUnit: Double,
    val flagEmoji: String,
    val countryName: String
)