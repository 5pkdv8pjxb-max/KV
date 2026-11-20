package com.example.kursovik.domain.mapper

import com.example.kursovik.data.model.CurrencyRate
import com.example.kursovik.domain.model.CurrencyRateDomain
import javax.inject.Inject

class CurrencyRateMapper @Inject constructor() {

    fun toDomain(rate: CurrencyRate): CurrencyRateDomain {
        return CurrencyRateDomain(
            charCode = rate.charCode,
            name = rate.name,
            value = rate.value,
            nominal = rate.nominal,
            valuePerUnit = rate.valuePerUnit,
            date = rate.date,
            flagEmoji = CurrencyFlagMapper.getFlag(rate.charCode),
            countryName = CurrencyCountryMapper.getCountry(rate.charCode),
            updatedAt = rate.updatedAt
        )
    }

    fun toDomainList(rates: List<CurrencyRate>): List<CurrencyRateDomain> {
        return rates.map { toDomain(it) }
    }
}