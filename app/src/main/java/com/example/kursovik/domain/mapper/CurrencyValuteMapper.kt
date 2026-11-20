package com.example.kursovik.domain.mapper

object CurrencyValuteMapper {

    private val valuteIdMap = mapOf(
        "USD" to "R01235", "EUR" to "R01239", "GBP" to "R01035",
        "CNY" to "R01375", "JPY" to "R01820", "CHF" to "R01775",
        "TRY" to "R01700J", "KZT" to "R01335", "BYN" to "R01090B",
        "UAH" to "R01720", "AED" to "R01230", "AUD" to "R01010",
        "CAD" to "R01350", "SEK" to "R01770", "NOK" to "R01560",
        "PLN" to "R01565", "CZK" to "R01760", "INR" to "R01270",
        "KRW" to "R01815", "SGD" to "R01625"
    )

    fun getValuteId(charCode: String): String? {
        return valuteIdMap[charCode.uppercase()]
    }
}