package com.example.kursovik.data.model

data class CurrencyRate(
    val charCode: String,
    val name: String,
    val value: Double,
    val nominal: Int,
    val valuePerUnit: Double,
    val date: String,
    val cbrId: String = "",
    val updatedAt: Long = 0L
)
