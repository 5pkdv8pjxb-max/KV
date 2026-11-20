package com.example.kursovik

import com.example.kursovik.domain.model.HistoricalRateDomain
import com.example.kursovik.ui.screens.chart.calculateStatistics
import org.junit.Assert.*
import org.junit.Test

class ChartStatisticsTest {
    private fun rates(vararg values: Double) =
        values.mapIndexed { i, v ->
            HistoricalRateDomain("0${i + 1}.11.2026", v)
        }

    @Test fun calculatesMinMaxAverageAndChange() {
        val stats = calculateStatistics(rates(80.0, 84.0, 82.0, 81.0))!!
        assertEquals(80.0, stats.min, 0.00001)
        assertEquals(84.0, stats.max, 0.00001)
        assertEquals(81.75, stats.avg, 0.00001)
        assertEquals(1.25, stats.changePercent, 0.00001)
    }

    @Test fun negativeChangeForFallingRate() {
        val stats = calculateStatistics(rates(100.0, 95.0))!!
        assertEquals(-5.0, stats.changePercent, 0.00001)
    }

    @Test fun emptyHistoryGivesNoStatistics() {
        assertNull(calculateStatistics(emptyList()))
    }
}
