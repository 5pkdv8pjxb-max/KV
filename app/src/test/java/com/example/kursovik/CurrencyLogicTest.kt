package com.example.kursovik

import com.example.kursovik.data.parser.CbrXmlParser
import com.example.kursovik.domain.model.CurrencyRateDomain
import com.example.kursovik.domain.usecase.ConvertCurrencyUseCase
import org.junit.Assert.*
import org.junit.Test

class CurrencyLogicTest {
    private val parser = CbrXmlParser()
    private val rates = listOf(
        CurrencyRateDomain("USD", "Dollar", 90.0, 1, 90.0, "01.11.2026", "", ""),
        CurrencyRateDomain("JPY", "Yen", 60.0, 100, 0.6, "01.11.2026", "", "")
    )
    @Test fun convertsThroughRubleWithNominal() {
        val convert = ConvertCurrencyUseCase()
        assertEquals(180.0, convert(2.0, "USD", "RUB", rates).result, 0.00001)
        assertEquals(2.0, convert(180.0, "RUB", "USD", rates).result, 0.00001)
        assertEquals(150.0, convert(1.0, "USD", "JPY", rates).result, 0.00001)
    }
    @Test fun rejectsInvalidAmounts() {
        listOf(-1.0, Double.NaN, Double.POSITIVE_INFINITY).forEach { amount ->
            assertTrue(runCatching { ConvertCurrencyUseCase()(amount, "USD", "RUB", rates) }.isFailure)
        }
    }
    @Test fun normalizesDailyAndHistoricalQuotes() {
        val daily = parser.parseDailyRates("""<ValCurs Date="01.11.2026"><Valute ID="R01820"><CharCode>JPY</CharCode><Nominal>100</Nominal><Name>Yen</Name><Value>60,0000</Value></Valute></ValCurs>""").single()
        val history = parser.parseDynamicRates("""<ValCurs><Record Date="01.11.2026"><Nominal>100</Nominal><Value>60,0000</Value></Record></ValCurs>""").single()
        assertEquals("R01820", daily.cbrId)
        assertEquals(0.6, daily.valuePerUnit, 0.00001)
        assertEquals(daily.valuePerUnit, history.value, 0.00001)
    }
    @Test fun rejectsInvalidServerResponse() {
        assertTrue(runCatching { parser.parseDailyRates("<html>Error</html>") }.isFailure)
        assertTrue(runCatching { parser.parseDynamicRates("<html>Error</html>") }.isFailure)
    }
}
