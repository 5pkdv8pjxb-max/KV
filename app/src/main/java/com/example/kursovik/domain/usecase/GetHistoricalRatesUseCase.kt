package com.example.kursovik.domain.usecase

import com.example.kursovik.data.repository.CurrencyRepository
import com.example.kursovik.domain.model.HistoricalRateDomain
import javax.inject.Inject

class GetHistoricalRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(
        charCode: String,
        from: String,
        to: String
    ): List<HistoricalRateDomain> {
        return repository.getHistoricalRates(charCode, from, to).map { rate ->
            HistoricalRateDomain(
                date = rate.date,
                value = rate.value
            )
        }
    }
}