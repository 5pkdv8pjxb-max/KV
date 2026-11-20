package com.example.kursovik.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kursovik.ui.theme.ErrorColor
import com.example.kursovik.ui.theme.TextPrimary

@Composable
fun StaleBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ErrorColor.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = ErrorColor)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Данные могут быть устаревшими. Проверьте подключение к интернету.",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
        }
    }
}