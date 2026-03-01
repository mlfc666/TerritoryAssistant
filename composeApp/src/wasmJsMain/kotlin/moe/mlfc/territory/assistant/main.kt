package moe.mlfc.territory.assistant

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import moe.mlfc.territory.assistant.repository.DriverFactory
import moe.mlfc.territory.assistant.repository.WikiRepository

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // 这里的 document.body!! 会让 Compose 填满整个浏览器页面
    ComposeViewport(document.body!!) {
        val driverFactory = remember { DriverFactory() }
        var repository by remember { mutableStateOf<WikiRepository?>(null) }
        var error by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            try {
                val driver = driverFactory.createDriver()
                val database = WikiDatabase(driver)
                repository = WikiRepository(database)
            } catch (e: Exception) {
                error = e.message
                println("数据库加载失败: ${e.message}")
            }
        }

        when {
            error != null -> {
                // 显示错误信息
            }
            repository != null -> {
                App(repository = repository!!)
            }
            else -> {
                // 2MB 文件加载很快，但还是建议给个 Loading
                // Text("正在解压 Wiki 数据...")
            }
        }
    }
}