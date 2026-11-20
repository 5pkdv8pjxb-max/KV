package com.example.kursovik.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kursovik.ui.screens.chart.ChartScreen
import com.example.kursovik.ui.screens.converter.ConverterScreen
import com.example.kursovik.ui.screens.currencies.CurrencyListScreen
import com.example.kursovik.ui.screens.history.HistoryScreen

private val BOTTOM_BAR_ROUTES = setOf("converter", "currencies", "history")

@Composable
fun KursNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute == null || currentRoute in BOTTOM_BAR_ROUTES) {
                BottomNavBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "converter",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("converter") {
                ConverterScreen()
            }
            composable("currencies") {
                CurrencyListScreen(
                    onCurrencyClick = { charCode ->
                        navController.navigate("chart/$charCode")
                    }
                )
            }
            composable("history") {
                HistoryScreen()
            }
            composable(
                route = "chart/{charCode}",
                arguments = listOf(navArgument("charCode") { type = NavType.StringType })
            ) {
                ChartScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}