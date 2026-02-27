package moe.mlfc.onlinebot.territory_assistant

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform