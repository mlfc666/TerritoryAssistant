package moe.mlfc.territory.assistant.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import moe.mlfc.territory.assistant.Achievement
import moe.mlfc.territory.assistant.WikiDatabase


class WikiRepository(private val database: WikiDatabase) {

    /**
     * 根据名称查询成就
     * @param name 要查询的中文名称，例如 "贡献者"
     */
    suspend fun findAchievement(name: String): Achievement? {
        println("Web端开始查询: $name") // 调试信息
        val result = database.queriesQueries
            .getAchievementByName(nameZhCN = name)
            .awaitAsOneOrNull()

        println("查询结束，结果是否为空: ${result == null}")
        return result
    }
    // --- 新增：验证驱动是否存活（手动插入测试数据） ---
    suspend fun insertTestData(id: String, name: String) {
        try {
            println("Web端执行插入测试: $name")
            database.queriesQueries.insertTestRecord(id = id, name = name)
            println("插入成功！")
        } catch (e: Exception) {
            println("插入失败: ${e.message}")
        }
    }

    // --- 新增：获取成就总数 ---
    suspend fun getAchievementCount(): Long {
        return try {
            val count = database.queriesQueries.countAchievements().awaitAsOne()
            println("当前数据库内成就总数: $count")
            count
        } catch (e: Exception) {
            println("查询数量失败 (可能表还没建立): ${e.message}")
            0L
        }
    }

    // --- 终极武器：如果你想用“最土但最稳”的方法把 2MB 灌进去 ---
    // 这个方法可以让你在 UI 层读取 ByteArray 后，通过 SQL 循环插入
    // 虽然效率一般，但对于 2MB（几千条数据）来说，在 Wasm 端完全可以接受
    suspend fun bulkInsertAchievements(list: List<Achievement>) {
        database.transaction {
            list.forEach { item ->
                // 这里调用你的插入语句
                // database.queriesQueries.insertFullRecord(item)
            }
        }
    }
}