package com.example.kursovik.data.model

data class Currency(
    val charCode: String,
    val numCode: String,
    val nominal: Int,
    val name: String,
    val value: Double,
    val valuePerUnit: Double
)
