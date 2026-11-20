package com.example.kursovik.ui.screens.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.kursovik.domain.model.HistoricalRateDomain
import com.example.kursovik.ui.theme.AccentPrimary

@Composable
fun RateLineChart(
    points: List<HistoricalRateDomain>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val values = points.map { it.value.toFloat() }
    val minValue = values.minOrNull() ?: 0f
    val maxValue = values.maxOrNull() ?: 1f
    val range = (maxValue - minValue).takeIf { it > 0f } ?: 1f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = width / (values.size - 1).coerceAtLeast(1)

        // Строим путь по точкам (нормализуем значения в диапазон 0..1)
        val path = Path()
        values.forEachIndexed { index, value ->
            val normalized = (value - minValue) / range
            val x = index * stepX
            val y = height - (normalized * height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        // Градиентная заливка под линией
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    AccentPrimary.copy(alpha = 0.35f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = height
            )
        )

        // Сама линия
        drawPath(
            path = path,
            color = AccentPrimary,
            style = Stroke(width = 3f)
        )

        // Точки на линии
        values.forEachIndexed { index, value ->
            val normalized = (value - minValue) / range
            val x = index * stepX
            val y = height - (normalized * height)
            drawCircle(
                color = AccentPrimary,
                radius = 4f,
                center = Offset(x, y)
            )
        }
    }
}