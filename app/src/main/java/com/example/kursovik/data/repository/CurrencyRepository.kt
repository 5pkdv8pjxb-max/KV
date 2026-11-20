package com.example.kursovik.data.repository

import com.example.kursovik.data.model.CurrencyRate
import com.example.kursovik.data.model.HistoricalRate
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    fun getDailyRates(): Flow<List<CurrencyRate>>

    suspend fun getHistoricalRates(
        charCode: String,
        from: String,
        to: String
    ): List<HistoricalRate>
}