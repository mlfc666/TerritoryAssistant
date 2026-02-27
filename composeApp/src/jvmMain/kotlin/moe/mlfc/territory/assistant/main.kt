package moe.mlfc.territory.assistant

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "territory_assistant",
    ) {
        App()
    }
}