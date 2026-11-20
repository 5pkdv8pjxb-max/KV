package com.example.kursovik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kursovik.ui.navigation.KursNavHost
import com.example.kursovik.ui.theme.KursTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KursTheme {
                KursNavHost()
            }
        }
    }
}