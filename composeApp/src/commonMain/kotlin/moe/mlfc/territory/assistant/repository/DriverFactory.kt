package moe.mlfc.territory.assistant.repository

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    suspend fun createDriver(): SqlDriver
}