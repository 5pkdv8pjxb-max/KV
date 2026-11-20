package com.example.kursovik.domain.usecase

import com.example.kursovik.domain.model.ConversionResult
import com.example.kursovik.domain.model.CurrencyRateDomain
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor() {

    operator fun invoke(
        amount: Double,
        fromCharCode: String,
        toCharCode: String,
        rates: List<CurrencyRateDomain>
    ): ConversionResult {
        require(amount.isFinite() && amount >= 0) { "Введите неотрицательную конечную сумму" }
        val fromValuePerUnit = getValuePerUnit(fromCharCode, rates)
        val toValuePerUnit = getValuePerUnit(toCharCode, rates)

        val amountInRub = amount * fromValuePerUnit

        val result = if (toValuePerUnit != 0.0) {
            amountInRub / toValuePerUnit
        } else {
            0.0
        }

        val rate = if (toValuePerUnit != 0.0) {
            fromValuePerUnit / toValuePerUnit
        } else {
            0.0
        }

        val date = rates.firstOrNull { it.charCode.equals(fromCharCode, ignoreCase = true) }?.date
            ?: rates.firstOrNull { it.charCode.equals(toCharCode, ignoreCase = true) }?.date
            ?: ""

        require(result.isFinite()) { "Слишком большая сумма" }
        return ConversionResult(
            fromCurrency = fromCharCode,
            toCurrency = toCharCode,
            amount = amount,
            result = result,
            rate = rate,
            date = date,
            updatedAt = rates.firstOrNull()?.updatedAt ?: 0L
        )
    }

    private fun getValuePerUnit(
        charCode: String,
        rates: List<CurrencyRateDomain>
    ): Double {
        if (charCode.equals("RUB", ignoreCase = true)) {
            return 1.0
        }

        val rate = rates.firstOrNull { it.charCode.equals(charCode, ignoreCase = true) }
            ?: throw IllegalArgumentException(
                "Курс для валюты $charCode не найден в списке доступных курсов"
            )

        require(rate.valuePerUnit.isFinite() && rate.valuePerUnit > 0) { "Некорректный курс валюты $charCode" }
        return rate.valuePerUnit
    }
}