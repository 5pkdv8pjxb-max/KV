package com.example.kursovik.data.parser

import com.example.kursovik.data.model.CurrencyRate
import com.example.kursovik.data.model.HistoricalRate
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import javax.inject.Inject

class CbrXmlParser @Inject constructor() {

    fun parseDailyRates(xml: String): List<CurrencyRate> {
        val document = Jsoup.parse(xml, "", Parser.xmlParser())
        val date = document.select("ValCurs").attr("Date")
        val valuteElements = document.select("Valute")

        require(date.isNotBlank() && valuteElements.isNotEmpty()) { "Сервер вернул некорректные данные курсов" }
        return valuteElements.map { element ->
            val charCode = element.select("CharCode").text()
            val nominal = element.select("Nominal").text().toInt().also { require(it > 0) }
            val name = element.select("Name").text()
            val rawValue = element.select("Value").text().replace(",", ".")
            val value = rawValue.toDouble().also { require(it.isFinite() && it > 0) }
            val valuePerUnit = if (nominal != 0) value / nominal else value

            CurrencyRate(
                charCode = charCode,
                name = name,
                value = value,
                nominal = nominal,
                valuePerUnit = valuePerUnit,
                date = date,
                cbrId = element.attr("ID")
            )
        }
    }

    fun parseDynamicRates(xml: String): List<HistoricalRate> {
        val document = Jsoup.parse(xml, "", Parser.xmlParser())
        require(document.select("ValCurs").isNotEmpty()) { "Некорректный ответ истории курсов" }
        val records = document.select("Record")

        return records.map { element ->
            val date = element.attr("Date")
            val rawValue = element.select("Value").text().replace(",", ".")
            val value = rawValue.toDouble().also { require(it.isFinite() && it > 0) }
            val nominal = element.select("Nominal").text().toInt().also { require(it > 0) }
            HistoricalRate(date = date, value = value / nominal)
        }
    }
}