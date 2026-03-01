package moe.mlfc.territory.assistant.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import kotlinx.coroutines.channels.Channel
import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker
import territory_assistant.composeapp.generated.resources.Res
import territory_assistant.composeapp.generated.resources.Res.getUri

// TODO: 替换为你实际的 Res 资源类导入路径
// import moe.mlfc.territory.assistant.generated.resources.Res
// import moe.mlfc.territory.assistant.generated.resources.data_1_0_20260228_09

@OptIn(ExperimentalWasmJsInterop::class)
private fun createWorker(): Worker = js("""
    new Worker(new URL("./worker.js", import.meta.url))
""")

// Wasm 专用辅助函数：安全地从 JS 的 event.data 中读取 action 属性
@OptIn(ExperimentalWasmJsInterop::class)
private fun getAction(data: JsAny?): String? = js("data.action")

// Wasm 专用辅助函数：安全地创建一个 JS 对象来发送给 Worker
@OptIn(ExperimentalWasmJsInterop::class)
private fun createInitCommand(url: String): JsAny = js("({ action: 'init_custom_db', url: url })")


actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        println("DEBUG: 准备启动 Worker...")

        val worker = createWorker()

        // 手动测试：监听 Worker 是否报错
        worker.onerror = { e ->
            println("DEBUG: Worker 加载失败！请检查网络面板。错误详情: $e")
        }

        println("DEBUG: 正在获取 2MB DB 文件的网络路径...")

        // 1. 获取 composeResources 中物理 db 文件的真实 URL

        // 1. 获取 composeResources 中物理 db 文件的真实 URL


        // 1. 获取 composeResources 中物理 db 文件的真实 URL
        // 1. 获取 composeResources 中物理 db 文件的真实 URL
        val dbUrl = getUri("files/database/data_1.0.20260228.09.db")
        println("DEBUG: 成功获取 DB URL: $dbUrl")

        // 使用 Channel 挂起当前协程，直到 Worker 通知我们加载完成
        val readyChannel = Channel<Unit>()

        // 2. 临时监听 Worker 发回的消息
        worker.onmessage = { event: MessageEvent ->
            // 解析 Worker 传回来的 JS 对象
            val action = getAction(event.data)
            if (action == "init_complete") {
                println("DEBUG: 收到 Worker 的 init_complete 消息！数据库成功装载入内存。")
                // 通知协程继续往下走
                readyChannel.trySend(Unit)
            }
        }

        println("DEBUG: 向 Worker 发送 init_custom_db 指令...")
        // 3. 将初始化指令和 DB 的 URL 发送给 worker.js 去 fetch 下载
        val initCmd = createInitCommand(dbUrl)
        worker.postMessage(initCmd)

        // 4. 挂起等待！这里会一直阻塞，直到 readyChannel 收到消息 (即 Worker 下载并装载完那 2MB 的文件)
        readyChannel.receive()
        println("DEBUG: 数据库挂起加载完毕！准备移交控制权...")

        // 5. 【极其关键】移交控制权给 SQLDelight
        // 必须清空我们的自定义监听，否则会拦截掉后续 SQLDelight 驱动自己注册的回调导致查询无响应
        worker.onmessage = null

        println("DEBUG: Worker 实例已准备就绪，正在初始化 WebWorkerDriver...")
        val driver = WebWorkerDriver(worker)
        println("DEBUG: Driver 已返回！")

        return driver
    }
}