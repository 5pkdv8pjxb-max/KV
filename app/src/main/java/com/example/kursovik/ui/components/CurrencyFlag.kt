package com.example.kursovik.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun CurrencyFlag(
    emoji: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 28.sp
) {
    Text(text = emoji, fontSize = fontSize, modifier = modifier)
}