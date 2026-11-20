package com.example.kursovik.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RateUpdateLabel(updatedAt: Long) {
    if (updatedAt > 0) {
        Text(
            "Обновлено: " + SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(updatedAt)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
