package com.example.kursovik

import com.example.kursovik.data.local.*
import com.example.kursovik.data.remote.*
import com.example.kursovik.data.repository.CurrencyRepositoryImpl
import com.example.kursovik.data.parser.CbrXmlParser
import com.example.kursovik.domain.mapper.*
import com.example.kursovik.domain.usecase.GetDailyRatesUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Assert.*
import org.junit.Test

class OfflineRatesTest {
    private fun xml(value: Int) = """<ValCurs Date="14.11.2026"><Valute ID="R01235"><CharCode>USD</CharCode><Nominal>1</Nominal><Name>Dollar</Name><Value>$value,0000</Value></Valute></ValCurs>"""
    private class MemoryDao : RateDao {
        val rows = MutableStateFlow<List<RateEntity>>(emptyList())
        override fun getAllRates(): Flow<List<RateEntity>> = rows
        override suspend fun insertAll(rates: List<RateEntity>) { rows.value = rates }
        override suspend fun clearAll() { rows.value = emptyList() }
        override suspend fun replaceAll(rates: List<RateEntity>) { rows.value = rates }
    }
    private class Connection : ConnectivityObserver {
        override val isOnline = MutableStateFlow(false)
    }
    private class Api(var response: String) : CbrApi {
        var calls = 0
        override suspend fun getDailyRates(date: String?): String { calls++; return response }
        override suspend fun getDynamicRates(date1: String, date2: String, valuteCode: String) = "<ValCurs/>"
    }
    @Test fun firstOfflineLaunchSeedsDatabase() = runBlocking {
        val dao = MemoryDao(); val api = Api(xml(90))
        val repo = CurrencyRepositoryImpl(api, dao, CbrXmlParser(), Connection(), InitialRatesSource { xml(80) })
        val rates = withTimeout(3000) { repo.getDailyRates().first() }
        assertEquals(80.0, rates.single().value, 0.0)
        assertEquals(80.0, dao.rows.value.single().value, 0.0)
        assertEquals(0, api.calls)
    }
    @Test fun offlineRestartUsesSavedRatesInsteadOfSeed() = runBlocking {
        val dao = MemoryDao()
        dao.rows.value = listOf(RateEntity("USD", "Dollar", 1, 88.0, 88.0, "14.11.2026", "R01235"))
        val repo = CurrencyRepositoryImpl(Api(xml(90)), dao, CbrXmlParser(), Connection(), InitialRatesSource { error("Seed must not overwrite saved rates") })
        assertEquals(88.0, withTimeout(3000) { repo.getDailyRates().first() }.single().value, 0.0)
    }
    @Test fun reconnectUpdatesDatabaseAndAllObservers() = runBlocking {
        val dao = MemoryDao(); val api = Api(xml(90)); val connection = Connection()
        val repo = CurrencyRepositoryImpl(api, dao, CbrXmlParser(), connection, InitialRatesSource { xml(80) })
        val first = async { withTimeout(3000) { repo.getDailyRates().first { it.single().value == 90.0 } } }
        val second = async { withTimeout(3000) { repo.getDailyRates().first { it.single().value == 90.0 } } }
        withTimeout(3000) { dao.rows.first { it.isNotEmpty() } }
        connection.isOnline.value = true
        first.await(); second.await()
        assertEquals(90.0, dao.rows.value.single().value, 0.0)
        assertEquals(1, api.calls)
    }
    @Test fun invalidNetworkResponseDoesNotEraseSavedRates() = runBlocking {
        val dao = MemoryDao(); val api = Api("<html>Error</html>"); val connection = Connection()
        connection.isOnline.value = true
        val repo = CurrencyRepositoryImpl(api, dao, CbrXmlParser(), connection, InitialRatesSource { xml(80) })
        val job = launch { repo.getDailyRates().collect() }
        withTimeout(3000) { while(api.calls == 0) delay(10) }
        delay(300) // ждём, пока репозиторий обработает ответ сервера
        assertEquals(80.0, dao.rows.value.single().value, 0.0)
        assertTrue(job.isActive)
        job.cancelAndJoin()
    }
    @Test fun everyPdfCurrencyHasFlagAndCountry() {
        "AUD AZN DZD AMD THB BHD BYN BOB BRL KRW HKD UAH DKK AED USD VND EUR EGP PLN JPY INR IRR CAD QAR CUP MMK GEL MDL NGN NZD TMT NOK OMR RON IDR ZAR SAR XDR RSD SGD KGS TJS BDT KZT MNT TRY UZS HUF GBP CZK SEK CHF ETB CNY".split(" ").forEach {
            assertNotEquals(it, "🏳️", CurrencyFlagMapper.getFlag(it))
            assertNotEquals(it, "Неизвестно", CurrencyCountryMapper.getCountry(it))
        }
    }
    @Test fun sortsLiveAndCachedRatesInPdfOrder() = runBlocking {
        val dao = MemoryDao(); val parser = CbrXmlParser()
        val items = listOf("CNY", "USD", "AZN", "AUD").map { code -> parser.parseDailyRates(xml(80).replace("USD", code)).single() }
        val repo = object : com.example.kursovik.data.repository.CurrencyRepository {
            override fun getDailyRates() = flowOf(items)
            override suspend fun getHistoricalRates(charCode: String, from: String, to: String) = emptyList<com.example.kursovik.data.model.HistoricalRate>()
        }
        assertEquals(listOf("AUD", "AZN", "USD", "CNY"), GetDailyRatesUseCase(repo, CurrencyRateMapper())().first().map { it.charCode })
    }

    @Test fun successfulCheckUpdatesTimestampEvenWhenQuoteIsUnchanged() = runBlocking {
        val dao = MemoryDao(); val connection = Connection()
        val repo = CurrencyRepositoryImpl(Api(xml(80)), dao, CbrXmlParser(), connection, InitialRatesSource { xml(80) })
        val observer = async { withTimeout(3000) { repo.getDailyRates().first { it.single().updatedAt > 0 } } }
        withTimeout(3000) { dao.rows.first { it.isNotEmpty() } }
        assertEquals(0L, dao.rows.value.single().updatedAt)
        val before = System.currentTimeMillis()
        connection.isOnline.value = true
        val refreshed = observer.await().single()
        assertEquals("14.11.2026", refreshed.date)
        assertEquals(80.0, refreshed.value, 0.0)
        assertTrue(refreshed.updatedAt >= before)
        assertEquals(refreshed.updatedAt, dao.rows.value.single().updatedAt)
        connection.isOnline.value = false
        val restarted = CurrencyRepositoryImpl(Api(xml(100)), dao, CbrXmlParser(), connection, InitialRatesSource { error("Existing DB") })
        assertEquals(refreshed.updatedAt, withTimeout(3000) { restarted.getDailyRates().first() }.single().updatedAt)
    }
}
