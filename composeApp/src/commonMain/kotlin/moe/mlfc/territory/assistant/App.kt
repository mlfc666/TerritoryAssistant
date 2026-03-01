package moe.mlfc.territory.assistant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.mlfc.territory.assistant.repository.WikiRepository
import moe.mlfc.territory.assistant.ui.theme.TerritoryAssistantTheme

@Composable
fun App(repository: WikiRepository) {
    // 状态管理：存储查询到的成就
    var searchQuery by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Achievement?>(null) }
    val scope = rememberCoroutineScope()

    TerritoryAssistantTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("输入成就名称 (如: 贡献者)") }
            )

            Button(onClick = {
                // 在后台线程执行查询
                scope.launch(Dispatchers.Default) {
                    result = repository.findAchievement(searchQuery)
                }
            }) {
                Text("查询")
            }
            val scope = rememberCoroutineScope()

            Button(onClick = {
                scope.launch {
                    // 1. 先往内存库里塞一条数据
                    repository.insertTestData("1", "测试数据")
                    println("DEBUG: 测试数据插入成功")

                    // 2. 尝试查询这条数据
                    val testResult = repository.findAchievement("测试数据")
                    if (testResult != null) {
                        println("DEBUG: 查询成功！得到内容: ${testResult.nameZhCN}")
                    } else {
                        println("DEBUG: 查询失败，结果依然为空")
                    }
                }
            }) {
                Text("点击验证数据库是否存活")
            }
            Button(onClick = {
                scope.launch {
                    if (repository.getAchievementCount() == 0L) {
                        println("检测到空数据库，开始从资源文件同步...")
                        // 这里的同步逻辑取决于你打算怎么把 2MB 塞进去
                        // 方案 A: 既然是只读，你可以直接把数据库导出成 .sql 脚本放在资源里执行
                        // 方案 B: 使用 sql.js 专用的 init 消息（需要修改 DriverFactory）
                    }
                }
            }) {
                Text("点击jiance")
            }
            Spacer(Modifier.height(20.dp))

            // 显示结果
            result?.let { item ->
                Card {
                    Column(Modifier.padding(8.dp)) {
                        Text("ID: ${item.id}")
                        // 访问带横线的字段（SQLDelight 生成的代码会使用反引号）
                        Text("描述: ${item.descZhCN ?: "无"}")
                        Text("类型: ${item.type}")
                    }
                }
            } ?: Text("未找到结果")
        }
    }
}