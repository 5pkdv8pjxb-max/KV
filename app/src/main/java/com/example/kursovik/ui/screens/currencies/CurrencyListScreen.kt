package com.example.kursovik.ui.screens.currencies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kursovik.ui.components.CurrencyRow
import com.example.kursovik.ui.components.ErrorView
import com.example.kursovik.ui.components.LoadingIndicator
import com.example.kursovik.ui.theme.DividerColor

@Composable
fun CurrencyListScreen(
    onCurrencyClick: (String) -> Unit,
    viewModel: CurrencyListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Все валюты", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChanged,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("Поиск по коду, названию, стране") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        state.allRates.firstOrNull()?.let {
            com.example.kursovik.ui.components.RateUpdateLabel(it.updatedAt)
        }

        when {
            state.isLoading && state.allRates.isEmpty() -> LoadingIndicator(Modifier.fillMaxSize())
            state.error != null && state.allRates.isEmpty() -> ErrorView(
                message = state.error ?: "Ошибка",
                onRetry = viewModel::load,
                modifier = Modifier.fillMaxSize()
            )
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (state.favoriteRates.isNotEmpty()) {
                        item {
                            Text(
                                "Избранные",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(state.favoriteRates, key = { "fav_${it.charCode}" }) { currency ->
                            CurrencyRow(
                                currency = currency,
                                isFavorite = true,
                                onFavoriteClick = { viewModel.toggleFavorite(currency.charCode) },
                                onClick = { onCurrencyClick(currency.charCode) }
                            )
                        }
                        item {
                            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }

                    item {
                        Text(
                            "Все валюты",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(state.allRates, key = { it.charCode }) { currency ->
                        CurrencyRow(
                            currency = currency,
                            isFavorite = state.favorites.contains(currency.charCode),
                            onFavoriteClick = { viewModel.toggleFavorite(currency.charCode) },
                            onClick = { onCurrencyClick(currency.charCode) }
                        )
                    }
                }
            }
        }
    }
}