package com.example.kursovik.domain.mapper

object CurrencyCountryMapper {

    private val countryMap = mapOf(
        "DZD" to "Алжир", "BHD" to "Бахрейн", "BOB" to "Боливия", "IRR" to "Иран", "CUP" to "Куба", "MMK" to "Мьянма", "NGN" to "Нигерия", "NZD" to "Новая Зеландия", "OMR" to "Оман", "BDT" to "Бангладеш", "ETB" to "Эфиопия",
        "USD" to "США", "EUR" to "Еврозона", "GBP" to "Великобритания",
        "CNY" to "Китай", "JPY" to "Япония", "CHF" to "Швейцария",
        "KZT" to "Казахстан", "BYN" to "Беларусь", "UAH" to "Украина",
        "TRY" to "Турция", "AED" to "ОАЭ", "KRW" to "Южная Корея",
        "INR" to "Индия", "BRL" to "Бразилия", "ZAR" to "ЮАР",
        "VND" to "Вьетнам", "THB" to "Таиланд", "IDR" to "Индонезия",
        "MYR" to "Малайзия", "SGD" to "Сингапур", "HKD" to "Гонконг",
        "MNT" to "Монголия", "TJS" to "Таджикистан", "UZS" to "Узбекистан",
        "AZN" to "Азербайджан", "GEL" to "Грузия", "AMD" to "Армения",
        "KGS" to "Кыргызстан", "MDL" to "Молдова", "TMT" to "Туркменистан",
        "EGP" to "Египет", "QAR" to "Катар", "SAR" to "Саудовская Аравия",
        "CZK" to "Чехия", "PLN" to "Польша", "HUF" to "Венгрия",
        "RON" to "Румыния", "BGN" to "Болгария", "RSD" to "Сербия",
        "AUD" to "Австралия", "CAD" to "Канада", "SEK" to "Швеция",
        "NOK" to "Норвегия", "DKK" to "Дания", "XDR" to "МВФ (СДР)",
        "RUB" to "Россия"
    )

    fun getCountry(charCode: String): String {
        return countryMap[charCode.uppercase()] ?: "Неизвестно"
    }
}