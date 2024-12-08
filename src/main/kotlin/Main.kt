import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.runBlocking
import service.Gemini
import ui.nav.NavigationHost
import utils.mapFolder
import utils.readDir

@Composable
@Preview
fun App() {
    NavigationHost()
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Readme.gen",
        state = WindowState(
            width = 800.dp,
            height = 800.dp
        )
    ) {
        App()
    }
}