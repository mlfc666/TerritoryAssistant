package moe.mlfc.territory.assistant.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MonochromeLightColorScheme = lightColorScheme(
    // 核心：主要操作和选中态使用黑色
    primary = DarkGrey,
    onPrimary = White,

    // 背景：使用极浅的灰色区分页面与卡片
    background = OffWhite,
    onBackground = DarkGrey,

    // 容器：卡片、弹窗使用纯白
    surface = White,
    onSurface = DarkGrey,

    // 变体文字：用于 ID 100001 这种次要信息
    onSurfaceVariant = MediumGrey,

    // 边框：用于截图中的细线边框
    outline = LightGrey,
    outlineVariant = LightGrey,

    // 错误色：仅在失败/警告时出现
    error = ErrorRed,
    onError = White
)

@Composable
fun TerritoryAssistantTheme(
    content: @Composable () -> Unit
) {
    // 强制使用浅色模式以符合截图风格
    MaterialTheme(
        colorScheme = MonochromeLightColorScheme,
        // 这里的 Typography 可以设置更简洁的无衬线字体
        content = content
    )
}