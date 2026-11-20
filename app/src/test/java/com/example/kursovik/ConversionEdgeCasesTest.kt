package com.example.kursovik

import com.example.kursovik.domain.model.CurrencyRateDomain
import com.example.kursovik.domain.usecase.ConvertCurrencyUseCase
import com.example.kursovik.domain.usecase.SearchCurrenciesUseCase
import org.junit.Assert.*
import org.junit.Test

class ConversionEdgeCasesTest {
    private val rates = listOf(
        CurrencyRateDomain("USD", "Доллар США", 90.0, 1, 90.0,
            "01.11.2026", "", "США"),
        CurrencyRateDomain("CAD", "Канадский доллар", 60.0, 1, 60.0,
            "01.11.2026", "", "Канада"),
        CurrencyRateDomain("EUR", "Евро", 100.0, 1, 100.0,
            "01.11.2026", "", "Европейский союз")
    )
    private val convert = ConvertCurrencyUseCase()

    @Test fun zeroAmountGivesZeroResult() {
        val result = convert(0.0, "USD", "EUR", rates)
        assertEquals(0.0, result.result, 0.0)
        assertEquals(0.9, result.rate, 0.00001)
    }

    @Test fun overflowIsRejected() {
        val error = runCatching {
            convert(Double.MAX_VALUE, "USD", "RUB", rates)
        }.exceptionOrNull()
        assertTrue(error is IllegalArgumentException)
        assertEquals("Слишком большая сумма", error?.message)
    }

    @Test fun unknownCurrencyIsRejected() {
        val error = runCatching {
            convert(1.0, "XYZ", "RUB", rates)
        }.exceptionOrNull()
        assertTrue(error is IllegalArgumentException)
    }

    @Test fun invalidRateIsRejected() {
        val broken = rates + CurrencyRateDomain("BAD", "Ошибочный курс",
            0.0, 1, 0.0, "01.11.2026", "", "")
        val error = runCatching {
            convert(1.0, "BAD", "RUB", broken)
        }.exceptionOrNull()
        assertTrue(error is IllegalArgumentException)
        assertEquals("Некорректный курс валюты BAD", error?.message)
    }

    @Test fun rubToRubKeepsAmount() {
        assertEquals(250.0,
            convert(250.0, "RUB", "RUB", rates).result, 0.0)
    }

    @Test fun searchFindsByNameCodeAndCountry() {
        val search = SearchCurrenciesUseCase()
        assertEquals(listOf("USD", "CAD"),
            search(rates, "доллар").map { it.charCode })
        assertEquals(listOf("EUR"),
            search(rates, " eur ").map { it.charCode })
        assertEquals(listOf("CAD"),
            search(rates, "Канада").map { it.charCode })
        assertEquals(3, search(rates, "   ").size)
    }
}
