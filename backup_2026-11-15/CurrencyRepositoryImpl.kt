package com.example.kursovik.data.repository

import com.example.kursovik.data.local.RateDao
import com.example.kursovik.data.local.RateEntity
import com.example.kursovik.data.model.CurrencyRate
import com.example.kursovik.data.model.HistoricalRate
import com.example.kursovik.data.parser.CbrXmlParser
import com.example.kursovik.data.remote.CbrApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.example.kursovik.data.remote.ConnectivityObserver
import com.example.kursovik.data.local.InitialRatesSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val api: CbrApi,
    private val rateDao: RateDao,
    private val parser: CbrXmlParser,
    private val networkMonitor: ConnectivityObserver,
    private val initialRatesSource: InitialRatesSource
) : CurrencyRepository {

    private val updateMutex = Mutex()
    private var lastSuccessfulUpdate = 0L

    // Room is the source of truth: both screens observe the same persisted data.
    override fun getDailyRates(): Flow<List<CurrencyRate>> = channelFlow {
        updateMutex.withLock {
            if (rateDao.getAllRates().first().isEmpty()) {
                val xml = initialRatesSource.readXml()
                rateDao.replaceAll(parser.parseDailyRates(xml).map { it.toRateEntity() })
            }
        }
        launch {
            rateDao.getAllRates().collect { rows ->
                if (rows.isNotEmpty()) send(rows.map { it.toCurrencyRate() })
            }
        }
        networkMonitor.isOnline.collectLatest { online ->
            if (online) {
                while (true) {
                    try {
                        updateMutex.withLock {
                            // Avoid duplicate requests from converter and currency-list observers.
                            if (System.currentTimeMillis() - lastSuccessfulUpdate > 5_000L) {
                                val fresh = parser.parseDailyRates(api.getDailyRates())
                                val checkedAt = System.currentTimeMillis()
                                rateDao.replaceAll(fresh.map { it.copy(updatedAt = checkedAt).toRateEntity() })
                                lastSuccessfulUpdate = System.currentTimeMillis()
                            }
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        // Retain the last complete snapshot. Retry while connected.
                    }
                    delay(60_000L)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getHistoricalRates(
        charCode: String,
        from: String,
        to: String
    ): List<HistoricalRate> {
        val valuteId = rateDao.getAllRates().first().firstOrNull { it.charCode == charCode }
            ?.cbrId?.takeIf { it.isNotBlank() }
            ?: parser.parseDailyRates(api.getDailyRates()).firstOrNull { it.charCode == charCode }?.cbrId?.takeIf { it.isNotBlank() }
            ?: CURRENCY_ID_MAP[charCode]
            ?: throw IllegalArgumentException(
                "Неизвестный внутренний ID валюты ЦБ для кода: $charCode"
            )

        val xml = api.getDynamicRates(
            date1 = from,
            date2 = to,
            valuteCode = valuteId
        )
        return parser.parseDynamicRates(xml)
    }

    private fun RateEntity.toCurrencyRate() = CurrencyRate(
        charCode = charCode,
        name = name,
        value = value,
        nominal = nominal,
        valuePerUnit = valuePerUnit,
        date = date,
        cbrId = cbrId,
        updatedAt = updatedAt
    )

    private fun CurrencyRate.toRateEntity() = RateEntity(
        charCode = charCode,
        name = name,
        nominal = nominal,
        value = value,
        valuePerUnit = valuePerUnit,
        date = date,
        cbrId = cbrId,
        updatedAt = updatedAt
    )

    companion object {
        private val CURRENCY_ID_MAP = mapOf(
            "USD" to "R01235",
            "EUR" to "R01239",
            "GBP" to "R01035",
            "CNY" to "R01375",
            "JPY" to "R01820",
            "CHF" to "R01775",
            "TRY" to "R01700J",
            "KZT" to "R01335",
            "BYN" to "R01090B",
            "UAH" to "R01720",
            "AED" to "R01230",
            "AUD" to "R01010",
            "CAD" to "R01350",
            "SEK" to "R01770",
            "NOK" to "R01560",
            "PLN" to "R01565",
            "CZK" to "R01760",
            "INR" to "R01270",
            "KRW" to "R01815",
            "SGD" to "R01625"
        )
    }
}