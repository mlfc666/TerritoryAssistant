package moe.mlfc.territory.assistant

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform