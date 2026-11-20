package com.example.kursovik.ui.screens.converter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kursovik.domain.model.ConversionResult
import com.example.kursovik.ui.components.CurrencyPickerBottomSheet
import com.example.kursovik.ui.components.ErrorView
import com.example.kursovik.ui.components.LoadingIndicator
import com.example.kursovik.ui.components.StaleBanner
import com.example.kursovik.ui.theme.AccentPrimary
import com.example.kursovik.ui.theme.SurfaceColor
import com.example.kursovik.ui.theme.TextPrimary
import com.example.kursovik.ui.theme.TextSecondary

@Composable
fun ConverterScreen(viewModel: ConverterViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Конвертер валют", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (state.isStale) {
            StaleBanner()
            Spacer(Modifier.height(8.dp))
        }

        when {
            state.isLoading && state.rates.isEmpty() -> LoadingIndicator(Modifier.height(300.dp))
            state.error != null && state.rates.isEmpty() -> ErrorView(
                message = state.error ?: "Ошибка",
                onRetry = viewModel::loadRates,
                modifier = Modifier.height(300.dp)
            )
            else -> {
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = viewModel::onAmountChanged,
                    label = { Text("Сумма") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CurrencySelectBox(
                        label = "Из валюты",
                        code = state.fromCharCode,
                        modifier = Modifier.weight(1f),
                        onClick = { showFromPicker = true }
                    )

                    IconButton(onClick = viewModel::swapCurrencies) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Поменять местами",
                            tint = AccentPrimary
                        )
                    }

                    CurrencySelectBox(
                        label = "В валюту",
                        code = state.toCharCode,
                        modifier = Modifier.weight(1f),
                        onClick = { showToPicker = true }
                    )
                }

                Spacer(Modifier.height(24.dp))

                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                state.conversionResult?.let { result ->
                    ResultCard(result = result)

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = viewModel::saveToHistory,
                        enabled = !state.savedToHistory,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.savedToHistory) "Сохранено ✓" else "Сохранить в историю")
                    }
                }
            }
        }
    }

    if (showFromPicker) {
        CurrencyPickerBottomSheet(
            rates = state.rates,
            onDismiss = { showFromPicker = false },
            onSelect = viewModel::onFromCurrencySelected
        )
    }
    if (showToPicker) {
        CurrencyPickerBottomSheet(
            rates = state.rates,
            onDismiss = { showToPicker = false },
            onSelect = viewModel::onToCurrencySelected
        )
    }
}

@Composable
private fun CurrencySelectBox(
    label: String,
    code: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier.padding(horizontal = 4.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(code, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        }
    }
}

@Composable
private fun ResultCard(result: ConversionResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "%.2f %s".format(result.amount, result.fromCurrency),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = "= %.2f %s".format(result.result, result.toCurrency),
                style = MaterialTheme.typography.headlineMedium,
                color = AccentPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "1 ${result.fromCurrency} = %.4f ${result.toCurrency}".format(result.rate),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            com.example.kursovik.ui.components.RateUpdateLabel(result.updatedAt)
            if (result.date.isNotBlank()) {
                Text(
                    text = "Курс ЦБ на ${result.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}