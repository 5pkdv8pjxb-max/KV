package com.example.kursovik.ui.screens.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kursovik.ui.components.ErrorView
import com.example.kursovik.ui.components.LoadingIndicator
import com.example.kursovik.ui.theme.ErrorColor
import com.example.kursovik.ui.theme.SuccessColor
import com.example.kursovik.ui.theme.SurfaceColor
import com.example.kursovik.ui.theme.TextPrimary
import com.example.kursovik.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    onBackClick: () -> Unit,
    viewModel: ChartViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Кнопка "Назад"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Назад",
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(state.flagEmoji, fontSize = 32.sp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(state.charCode, style = MaterialTheme.typography.headlineSmall)
                Text(state.countryName, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }

        Spacer(Modifier.height(16.dp))

        PeriodSelector(selected = state.period, onSelect = viewModel::onPeriodSelected)

        Spacer(Modifier.height(16.dp))

        when {
            state.isLoading -> LoadingIndicator(Modifier.fillMaxSize())
            state.error != null -> ErrorView(
                message = state.error ?: "Ошибка",
                onRetry = { viewModel.onPeriodSelected(state.period) },
                modifier = Modifier.fillMaxSize()
            )
            state.history.isEmpty() -> Text(
                "Нет данных за выбранный период",
                color = TextSecondary,
                modifier = Modifier.padding(top = 32.dp)
            )
            else -> {
                Text("Рубли за 1 единицу валюты", color = TextSecondary)
                RateLineChart(points = state.history, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(24.dp))

                state.statistics?.let { StatisticsSection(it) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodSelector(selected: ChartPeriod, onSelect: (ChartPeriod) -> Unit) {
    val periods = ChartPeriod.entries
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        periods.forEachIndexed { index, period ->
            SegmentedButton(
                selected = period == selected,
                onClick = { onSelect(period) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = periods.size)
            ) {
                Text(period.label)
            }
        }
    }
}

@Composable
private fun StatisticsSection(stats: ChartStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            StatRow("Минимум", "%.4f ₽".format(stats.min))
            StatRow("Максимум", "%.4f ₽".format(stats.max))
            StatRow("Средний", "%.4f ₽".format(stats.avg))
            StatRow(
                label = "Изменение",
                value = "%+.2f%%".format(stats.changePercent),
                valueColor = if (stats.changePercent >= 0) SuccessColor else ErrorColor
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = valueColor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}