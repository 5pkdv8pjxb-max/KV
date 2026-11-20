package com.example.kursovik.ui.screens.chart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursovik.domain.mapper.CurrencyCountryMapper
import com.example.kursovik.domain.mapper.CurrencyFlagMapper
import com.example.kursovik.domain.model.HistoricalRateDomain
import com.example.kursovik.domain.usecase.GetHistoricalRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

enum class ChartPeriod(val label: String, val days: Int) {
    WEEK("1W", 7),
    MONTH("1M", 30),
    THREE_MONTHS("3M", 90),
    SIX_MONTHS("6M", 180),
    YEAR("1Y", 365);

    fun toDateRange(): Pair<String, String> {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val to = sdf.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val from = sdf.format(calendar.time)
        return from to to
    }
}

data class ChartStatistics(
    val min: Double,
    val max: Double,
    val avg: Double,
    val changePercent: Double
)

data class ChartUiState(
    val charCode: String,
    val flagEmoji: String = "",
    val countryName: String = "",
    val period: ChartPeriod = ChartPeriod.MONTH,
    val history: List<HistoricalRateDomain> = emptyList(),
    val statistics: ChartStatistics? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChartViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHistoricalRatesUseCase: GetHistoricalRatesUseCase
) : ViewModel() {

    private val charCode: String = checkNotNull(savedStateHandle["charCode"])

    private val _uiState = MutableStateFlow(
        ChartUiState(
            charCode = charCode,
            flagEmoji = CurrencyFlagMapper.getFlag(charCode),
            countryName = CurrencyCountryMapper.getCountry(charCode)
        )
    )
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()


    private var historyJob: Job? = null

    init {
        loadHistory(ChartPeriod.MONTH)
    }

    fun onPeriodSelected(period: ChartPeriod) {
        loadHistory(period)
    }


    private fun loadHistory(period: ChartPeriod) {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, period = period) }
            try {
                val (from, to) = period.toDateRange()
                val history = getHistoricalRatesUseCase(charCode, from, to)
                val statistics = calculateStatistics(history)

                _uiState.update {
                    it.copy(
                        history = history,
                        statistics = statistics,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Не удалось загрузить историю курса")
                }
            }
        }
    }

    private fun calculateStatistics(history: List<HistoricalRateDomain>): ChartStatistics? {
        if (history.isEmpty()) return null

        val values = history.map { it.value }
        val min = values.min()
        val max = values.max()
        val avg = values.average()
        val first = values.first()
        val last = values.last()
        val changePercent = if (first != 0.0) ((last - first) / first) * 100 else 0.0

        return ChartStatistics(min = min, max = max, avg = avg, changePercent = changePercent)
    }
}