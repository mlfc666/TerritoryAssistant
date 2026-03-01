package moe.mlfc.territory.assistant.repository

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import moe.mlfc.territory.assistant.WikiDatabase
import org.jetbrains.compose.resources.ExperimentalResourceApi
import territory_assistant.composeapp.generated.resources.Res

actual class DriverFactory(private val context: Context) {
    actual suspend fun createDriver(): SqlDriver {
        val dbFile = context.getDatabasePath("wiki.db")
        if (!dbFile.exists()) {
            // 从你那个深层路径读取

            // 2MB 的文件
            val bytes = Res.readBytes("files/database/data_1.0.20260228.09.db")
            dbFile.parentFile?.mkdirs()
            dbFile.writeBytes(bytes)
        }
        return AndroidSqliteDriver(WikiDatabase.Schema.synchronous(), context, "wiki.db")
    }
}