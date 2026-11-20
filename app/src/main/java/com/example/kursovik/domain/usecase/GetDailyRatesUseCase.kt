package com.example.kursovik.domain.usecase

import com.example.kursovik.data.repository.CurrencyRepository
import com.example.kursovik.domain.mapper.CurrencyRateMapper
import com.example.kursovik.domain.model.CurrencyRateDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDailyRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository,
    private val mapper: CurrencyRateMapper
) {
    operator fun invoke(): Flow<List<CurrencyRateDomain>> {
        return repository.getDailyRates().map { rates ->
            mapper.toDomainList(rates).sortedWith(compareBy(
                { com.example.kursovik.domain.mapper.CurrencyOrder.position(it.charCode) },
                { it.charCode }
            ))
        }
    }
}