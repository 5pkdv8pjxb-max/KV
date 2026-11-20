package com.example.kursovik.ui.screens.currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursovik.data.local.FavoriteDao
import com.example.kursovik.data.local.FavoriteEntity
import com.example.kursovik.domain.model.CurrencyRateDomain
import com.example.kursovik.domain.usecase.GetDailyRatesUseCase
import com.example.kursovik.domain.usecase.SearchCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject

data class CurrencyListUiState(
    val allRates: List<CurrencyRateDomain> = emptyList(),
    val favoriteRates: List<CurrencyRateDomain> = emptyList(),
    val favorites: Set<String> = emptySet(),
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CurrencyListViewModel @Inject constructor(
    private val getDailyRatesUseCase: GetDailyRatesUseCase,
    private val searchCurrenciesUseCase: SearchCurrenciesUseCase,
    private val favoriteDao: FavoriteDao
) : ViewModel() {

    private val _rates = MutableStateFlow<List<CurrencyRateDomain>>(emptyList())
    private val _query = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val favoritesFlow = favoriteDao.getAllFavorites()
        .map { list -> list.map { it.charCode }.toSet() }

    val uiState: StateFlow<CurrencyListUiState> = combine(
        _rates, _query, favoritesFlow, _isLoading, _error
    ) { rates, query, favorites, isLoading, error ->
        val filtered = searchCurrenciesUseCase(rates, query)
        CurrencyListUiState(
            allRates = filtered,
            favoriteRates = filtered.filter { favorites.contains(it.charCode) },
            favorites = favorites,
            query = query,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CurrencyListUiState()
    )

    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _error.value = null
            _isLoading.value = true
            getDailyRatesUseCase()
                .catch { e ->
                    _error.value = e.message ?: "Ошибка загрузки курсов"
                    _isLoading.value = false
                }
                .collect { rates ->
                    _rates.value = rates
                    _isLoading.value = false
                    _error.value = null
                }
        }
    }

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    fun toggleFavorite(charCode: String) {
        viewModelScope.launch {
            if (uiState.value.favorites.contains(charCode)) {
                favoriteDao.removeFavorite(charCode)
            } else {
                favoriteDao.addFavorite(FavoriteEntity(charCode))
            }
        }
    }
}