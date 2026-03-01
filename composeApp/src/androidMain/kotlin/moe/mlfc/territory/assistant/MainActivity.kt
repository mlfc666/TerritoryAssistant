package moe.mlfc.territory.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.mlfc.territory.assistant.repository.DriverFactory
import moe.mlfc.territory.assistant.repository.WikiRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val driverFactory = DriverFactory(this)

        setContent {
            // 创建一个状态来保存 Repository
            var repository by remember { mutableStateOf<WikiRepository?>(null) }

            // LaunchedEffect 会在 Composable 首次挂载时启动一个协程
            LaunchedEffect(Unit) {
                // 在协程中调用 suspend 函数
                val driver = driverFactory.createDriver()
                val database = WikiDatabase(driver)
                repository = WikiRepository(database)
            }

            // 根据状态显示不同的 UI
            if (repository != null) {
                App(repository!!)
            } else {
                // 数据库初始化时的加载界面
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    // 或者 Text("正在准备数据库...")
                }
            }
        }
    }
}