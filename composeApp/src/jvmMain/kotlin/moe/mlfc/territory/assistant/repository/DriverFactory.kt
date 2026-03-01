package moe.mlfc.territory.assistant.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import territory_assistant.composeapp.generated.resources.Res
import java.io.File

actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        val userHome = System.getProperty("user.home")
        val dbFile = File(userHome, ".mywiki/wiki.db")

        if (!dbFile.exists()) {
            dbFile.parentFile.mkdirs()
            val bytes = Res.readBytes("files/database/data_1.0.20260228.09.db")
            dbFile.writeBytes(bytes)
        }

        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        return driver
    }
}