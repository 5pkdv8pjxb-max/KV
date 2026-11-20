package com.example.kursovik.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Converter : BottomNavItem("converter", "Конвертер", Icons.Default.CurrencyExchange)
    data object Currencies : BottomNavItem("currencies", "Валюты", Icons.Default.List)
    data object History : BottomNavItem("history", "История", Icons.Default.History)
}

private val bottomNavItems = listOf(
    BottomNavItem.Converter,
    BottomNavItem.Currencies,
    BottomNavItem.History
)

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String?) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}