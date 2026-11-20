package com.example.kursovik.domain.mapper

/** Order of rows in the supplied CBR daily-rates PDF. New codes follow at the end. */
object CurrencyOrder {
    private val positions = listOf("AUD", "AZN", "DZD", "AMD", "THB", "BHD", "BYN", "BOB", "BRL", "KRW", "HKD", "UAH", "DKK", "AED", "USD", "VND", "EUR", "EGP", "PLN", "JPY", "INR", "IRR", "CAD", "QAR", "CUP", "MMK", "GEL", "MDL", "NGN", "NZD", "TMT", "NOK", "OMR", "RON", "IDR", "ZAR", "SAR", "XDR", "RSD", "SGD", "KGS", "TJS", "BDT", "KZT", "MNT", "TRY", "UZS", "HUF", "GBP", "CZK", "SEK", "CHF", "ETB", "CNY").withIndex().associate { it.value to it.index }
    fun position(code: String): Int = positions[code] ?: Int.MAX_VALUE
}
