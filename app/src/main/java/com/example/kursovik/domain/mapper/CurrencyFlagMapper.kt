package com.example.kursovik.domain.mapper

object CurrencyFlagMapper {

    private val flagMap = mapOf(
        "DZD" to "🇩🇿", "BHD" to "🇧🇭", "BOB" to "🇧🇴", "IRR" to "🇮🇷", "CUP" to "🇨🇺", "MMK" to "🇲🇲", "NGN" to "🇳🇬", "NZD" to "🇳🇿", "OMR" to "🇴🇲", "BDT" to "🇧🇩", "ETB" to "🇪🇹",
        "USD" to "🇺🇸", "EUR" to "🇪🇺", "GBP" to "🇬🇧", "CNY" to "🇨🇳",
        "JPY" to "🇯🇵", "CHF" to "🇨🇭", "KZT" to "🇰🇿", "BYN" to "🇧🇾",
        "UAH" to "🇺🇦", "TRY" to "🇹🇷", "AED" to "🇦🇪", "KRW" to "🇰🇷",
        "INR" to "🇮🇳", "BRL" to "🇧🇷", "ZAR" to "🇿🇦", "VND" to "🇻🇳",
        "THB" to "🇹🇭", "IDR" to "🇮🇩", "MYR" to "🇲🇾", "SGD" to "🇸🇬",
        "HKD" to "🇭🇰", "MNT" to "🇲🇳", "TJS" to "🇹🇯", "UZS" to "🇺🇿",
        "AZN" to "🇦🇿", "GEL" to "🇬🇪", "AMD" to "🇦🇲", "KGS" to "🇰🇬",
        "MDL" to "🇲🇩", "TMT" to "🇹🇲", "EGP" to "🇪🇬", "QAR" to "🇶🇦",
        "SAR" to "🇸🇦", "CZK" to "🇨🇿", "PLN" to "🇵🇱", "HUF" to "🇭🇺",
        "RON" to "🇷🇴", "BGN" to "🇧🇬", "RSD" to "🇷🇸", "AUD" to "🇦🇺",
        "CAD" to "🇨🇦", "SEK" to "🇸🇪", "NOK" to "🇳🇴", "DKK" to "🇩🇰",
        "XDR" to "🏦", "RUB" to "🇷🇺"
    )

    fun getFlag(charCode: String): String {
        return flagMap[charCode.uppercase()] ?: "🏳️"
    }
}