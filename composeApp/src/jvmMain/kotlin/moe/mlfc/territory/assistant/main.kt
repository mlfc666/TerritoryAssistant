package moe.mlfc.territory.assistant

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import moe.mlfc.territory.assistant.repository.DriverFactory
import moe.mlfc.territory.assistant.repository.WikiRepository

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "territory_assistant",
    ) {
        val driverFactory = DriverFactory()
        // 创建一个状态来保存 Repository
        var repository by remember { mutableStateOf<WikiRepository?>(null) }

        // LaunchedEffect 会在 Composable 首次挂载时启动一个协程
        LaunchedEffect(Unit) {
            // 在协程中调用 suspend 函数
            val driver = driverFactory.createDriver()
            val database = WikiDatabase(driver)
            repository = WikiRepository(database)
        }
        if (repository != null){
            App(repository = repository!!)
        }
    }
}