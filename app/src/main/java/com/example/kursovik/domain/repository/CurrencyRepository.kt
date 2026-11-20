package com.example.kursovik.domain.repository

import com.example.kursovik.domain.model.CurrencyRateDomain
import com.example.kursovik.domain.model.HistoricalRateDomain
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    fun getDailyRates(): Flow<List<CurrencyRateDomain>>

    suspend fun getHistoricalRates(
        charCode: String,
        from: String,
        to: String
    ): List<HistoricalRateDomain>
}