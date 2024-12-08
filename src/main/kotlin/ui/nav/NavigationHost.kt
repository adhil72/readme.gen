package ui.nav

import androidx.compose.runtime.*
import ui.screens.HomeScreen

@Composable
fun NavigationHost() {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(onNavigate = { currentScreen = it })
    }
}
