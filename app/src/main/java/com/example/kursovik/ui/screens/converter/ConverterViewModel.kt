package com.example.kursovik.ui.screens.converter

import com.example.kursovik.ui.toUserMessage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursovik.data.local.HistoryDao
import com.example.kursovik.data.local.HistoryEntity
import com.example.kursovik.domain.model.ConversionResult
import com.example.kursovik.domain.model.CurrencyRateDomain
import com.example.kursovik.domain.usecase.ConvertCurrencyUseCase
import com.example.kursovik.domain.usecase.GetDailyRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ConverterUiState(
    val amount: String = "1",
    val fromCharCode: String = "USD",
    val toCharCode: String = "RUB",
    val rates: List<CurrencyRateDomain> = emptyList(),
    val conversionResult: ConversionResult? = null,
    val isStale: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val savedToHistory: Boolean = false
)

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val getDailyRatesUseCase: GetDailyRatesUseCase,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val historyDao: HistoryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    private var ratesJob: Job? = null
    private var saving = false

    init {
        loadRates()
    }

    fun loadRates() {
        ratesJob?.cancel()
        ratesJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getDailyRatesUseCase()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.toUserMessage("Ошибка загрузки курсов"))
                    }
                }
                .collect { rates ->
                    // Плашка "Данные могут быть устаревшими" показывается только,
                    // если курс старше 3 дней. Это учитывает выходные и праздники ЦБ РФ,
                    // когда новые курсы не публикуются.
                    val isStale = rates.firstOrNull()?.let { rate ->
                        if (rate.updatedAt > 0) System.currentTimeMillis() - rate.updatedAt > 3 * 24L * 60 * 60 * 1000
                        else isOlderThan(rate.date, days = 3)
                    } ?: false

                    _uiState.update {
                        it.copy(
                            rates = rates,
                            isLoading = false,
                            isStale = isStale,
                            error = null
                        )
                    }
                    recalculate()
                }
        }
    }

    fun onAmountChanged(value: String) {
        // Приводим запятую к точке, чтобы toDoubleOrNull() работал корректно
        val normalized = value.replace(",", ".")
        _uiState.update { it.copy(amount = normalized, savedToHistory = false) }
        recalculate()
    }

    fun onFromCurrencySelected(code: String) {
        _uiState.update { it.copy(fromCharCode = code, savedToHistory = false) }
        recalculate()
    }

    fun onToCurrencySelected(code: String) {
        _uiState.update { it.copy(toCharCode = code, savedToHistory = false) }
        recalculate()
    }

    fun swapCurrencies() {
        _uiState.update {
            it.copy(
                fromCharCode = it.toCharCode,
                toCharCode = it.fromCharCode,
                savedToHistory = false
            )
        }
        recalculate()
    }

    /**
     * Пересчитывает результат конвертации на основе текущего состояния.
     * Конвертация выполняется через рубль как промежуточную валюту
     * (см. ConvertCurrencyUseCase).
     */
    private fun recalculate() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || !amount.isFinite() || amount < 0) {
            _uiState.update { it.copy(conversionResult = null, error = "Введите корректную неотрицательную сумму") }
            return
        }

        if (state.rates.isEmpty()) return

        try {
            val result = convertCurrencyUseCase(
                amount = amount,
                fromCharCode = state.fromCharCode,
                toCharCode = state.toCharCode,
                rates = state.rates
            )
            _uiState.update { it.copy(conversionResult = result, error = null) }
        } catch (e: IllegalArgumentException) {
            _uiState.update { it.copy(error = e.message, conversionResult = null) }
        }
    }

    fun saveToHistory() {
        val result = _uiState.value.conversionResult ?: return
        if (_uiState.value.savedToHistory || saving) return
        saving = true
        viewModelScope.launch {
          try {
            historyDao.insert(
                HistoryEntity(
                    fromCode = result.fromCurrency,
                    toCode = result.toCurrency,
                    amount = result.amount,
                    result = result.result,
                    timestamp = System.currentTimeMillis()
                )
            )
            _uiState.update { if (it.conversionResult == result) it.copy(savedToHistory = true) else it }
          } catch (e: Exception) {
            if (e is CancellationException) throw e
            _uiState.update { it.copy(error = "Не удалось сохранить конвертацию") }
          } finally { saving = false }
        }
    }

    /**
     * Возвращает true, если дата курса старше указанного количества дней.
     * Используется для определения "устаревших" данных: ЦБ РФ не публикует
     * курсы по выходным и праздникам, поэтому разница в 1–2 дня — норма.
     */
    private fun isOlderThan(cbrDate: String, days: Int): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = sdf.parse(cbrDate) ?: return false
            val diff = System.currentTimeMillis() - date.time
            diff > days * 24L * 60L * 60L * 1000L
        } catch (e: Exception) {
            false
        }
    }
}