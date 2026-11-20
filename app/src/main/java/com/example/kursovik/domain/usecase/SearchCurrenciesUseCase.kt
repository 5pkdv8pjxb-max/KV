package com.example.kursovik.domain.usecase

import com.example.kursovik.domain.model.CurrencyRateDomain
import javax.inject.Inject

class SearchCurrenciesUseCase @Inject constructor() {

    operator fun invoke(
        rates: List<CurrencyRateDomain>,
        query: String
    ): List<CurrencyRateDomain> {
        if (query.isBlank()) return rates

        val normalizedQuery = query.trim().lowercase()

        return rates.filter { rate ->
            rate.charCode.lowercase().contains(normalizedQuery) ||
                rate.name.lowercase().contains(normalizedQuery) ||
                rate.countryName.lowercase().contains(normalizedQuery)
        }
    }
}